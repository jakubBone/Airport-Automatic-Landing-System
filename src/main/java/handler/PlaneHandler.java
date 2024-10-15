package handler;

import airport.AirTrafficController;
import airport.Airport;
import airport.Runway;
import client.PlaneClient;
import exceptions.LocationAcquisitionException;
import location.Location;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;

import static handler.PlaneHandler.AirportInstruction.*;

@Log4j2
public class PlaneHandler extends Thread {

    public enum AirportInstruction {
        DESCENT,
        HOLD_PATTERN,
        LAND,
        FULL,
        COLLISION,
        OCCUPIED
    }

    private final Socket clientSocket;
    private final AirTrafficController controller;
    private final Airport airport;

    public PlaneHandler(Socket clientSocket, AirTrafficController controller, Airport airport) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.airport = airport;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            Plane plane = (Plane) in.readObject();

            if (!isPlaneRegistered(plane, out)){
                return;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
            handlePlaneMovement(plane, in, out);
        } catch (IOException | ClassNotFoundException | LocationAcquisitionException ex ) {
            log.error("Error occurred while handling client request: {}", ex.getMessage());
        }
    }

    private boolean isPlaneRegistered(Plane plane, ObjectOutputStream out) throws IOException {
        if (controller.isSpaceFull()) {
            out.writeObject(FULL);
            log.info("No capacity in airspace for Plane [{}]", plane.getId());
            return false;
        }
        if (controller.isLocationOccupied(plane)) {
            out.writeObject(OCCUPIED);
            log.info("Initial location Plane [{}] is occupied", plane.getId());
            return false;
        }
        controller.registerPlane(plane);
        log.info("Plane [{}] registered in airspace on [{}] / [{}] / [{}]", plane.getId(), plane.getLocation().getX(), plane.getLocation().getY(), plane.getLocation().getAltitude() );
        return true;
    }

    private void handlePlaneMovement(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, LocationAcquisitionException{
        while (true) {
            Object state = receivePlaneStateUpdate(in, plane);
            plane.burnFuel();

            if (plane.isDestroyed()) {
                out.writeObject(AirportInstruction.COLLISION);
                return;
            }

            if(isOutOfFuel(state)){
                plane.destroyPlane();
                controller.removePlaneFromSpace(plane);
                log.info("Plane [{}] is out of fuel. Disappeared from the radar ", plane.getId());
                return;
            }

            plane.setLocation((Location) state);

            if (isPlaneReadyToLand(plane)) {
                if(attemptLanding(plane, in, out)){
                    break;
                }
            } else {
                handleDescent(plane, out);
            }
        }
    }


    private boolean attemptLanding(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, LocationAcquisitionException, ClassNotFoundException {
        Runway runway = getRunwayIfPlaneInCorridor(plane);
        if(runway != null && controller.isRunwayAvailable(runway)){
            controller.assignRunway(runway);
            handleLanding(plane, runway, in, out);
            return true;
        }
        log.info("Plane [{}] is holding pattern", plane.getId());
        out.writeObject(HOLD_PATTERN);
        return false;
    }

    private boolean isPlaneReadyToLand(Plane plane) {
        return plane.getLocation().getAltitude() <= 1000;
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        log.info("Plane [{}] is descending", plane.getId());
        out.writeObject(DESCENT);
    }

    private Runway getRunwayIfPlaneInCorridor(Plane plane) {
        Location corridor1Entry = Airport.runway1.getCorridor().getEntryWaypoint();
        Location corridor2Entry = Airport.runway2.getCorridor().getEntryWaypoint();

        int corridor1EntryX = corridor1Entry.getX();
        int corridor1EntryY = corridor1Entry.getY();

        int corridor2EntryX = corridor2Entry.getX();
        int corridor2EntryY = corridor2Entry.getY();

        int planeX = plane.getLocation().getX();
        int planeY = plane.getLocation().getY();

        Runway runway = null;

        if (planeX == corridor1EntryX && planeY == corridor1EntryY) {
            runway = Airport.runway1;
        } else if (planeX == corridor2EntryX && planeY == corridor2EntryY) {
            runway = Airport.runway2;
        }

        return runway;
    }

    private boolean isOutOfFuel(Object state){
        if(state instanceof PlaneClient.PlaneStatus) {
            PlaneClient.PlaneStatus status = (PlaneClient.PlaneStatus) state;
            if (status == PlaneClient.PlaneStatus.OUT_OF_FUEL) {
              return true;
            }
        }
        return false;
    }

    private void handleLanding(Plane plane, Runway runway, ObjectInputStream in, ObjectOutputStream out) throws IOException, LocationAcquisitionException, ClassNotFoundException*/ {
        out.writeObject(LAND);
        out.writeObject(runway);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getId(), runway.getId());

        while (true) {
            Object state = receivePlaneStateUpdate(in, plane);

            if(isOutOfFuel(state)){
                plane.destroyPlane();
                log.info("Plane [{}] is out of fuel. Disappeared from the radar", plane.getId());
                break;
            }

            plane.setLocation((Location) state);
            log.info("Plane [{}] is landing", plane.getId());

            if(controller.hasLandedOnRunway(plane, runway)){
                log.info("Plane [{}] has successfully landed on runway [{}]", plane.getId(), runway.getId());
                break;
            }

            if(controller.isRunwayCollision(plane)){
                plane.destroyPlane();
                log.info("Runway collision detected for Plane [{}]:", plane.getId());
                break;
            }
        }
        completeLanding(plane, runway);
    }

    private void completeLanding(Plane plane, Runway runway) {
        controller.removePlaneFromSpace(plane);
        log.info("Plane [{}] removed from airspace", plane.getId());

        controller.releaseRunway(runway);
        log.info("Runway [{}] released", runway.getId());
    }

    public Object receivePlaneStateUpdate(ObjectInputStream in, Plane plane) throws LocationAcquisitionException, IOException, ClassNotFoundException {
        Object request = in.readObject();

        if (request instanceof Location || request instanceof PlaneClient.PlaneStatus) {
            return request;
        }

        throw new LocationAcquisitionException("Unexpected request type: " + request);
    }
}