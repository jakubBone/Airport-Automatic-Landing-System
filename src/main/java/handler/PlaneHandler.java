package handler;

import airport.AirTrafficController;
import airport.Airport;
import airport.Runway;
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
        DESCENT, HOLD_PATTERN, LAND, FULL, COLLISION, OCCUPIED
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

            managePlane(plane, in, out);
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

    private void managePlane(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, LocationAcquisitionException{
        while (true) {
            if (plane.isDestroyed()) {
                out.writeObject(AirportInstruction.COLLISION);
                return;
            }

            Object message = in.readObject();

            if(message instanceof String && message.equals("OUT_OF_FUEL")){
                handleOutOfFuel(plane);
                return;
            }

            if(message instanceof Location){
                plane.setLocation((Location) message);
            }

            if (isPlaneAtLandingAltitude(plane)) {
                if(attemptLanding(plane, in, out)){
                    break;
                }
            } else {
                handleDescent(plane, out);
            }
        }
    }


    private boolean attemptLanding(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, LocationAcquisitionException, ClassNotFoundException {
        Runway runway = getRunwayIfPlaneAtCorridor(plane);

        if(runway != null && controller.isRunwayAvailable(runway)){
            controller.assignRunway(runway);
            handleLanding(plane, runway, in, out);
            return true;
        }

        log.info("Plane [{}] is holding pattern", plane.getId());
        out.writeObject(HOLD_PATTERN);
        return false;
    }


    private boolean isPlaneAtLandingAltitude(Plane plane) {
        return plane.getLocation().getAltitude() <= 1000;
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        out.writeObject(DESCENT);
        log.info("Plane [{}] is descending", plane.getId());
    }

    private Runway getRunwayIfPlaneAtCorridor(Plane plane) {
        Location runway1Corridor = Airport.runway1.getCorridor().getEntryWaypoint();
        Location runway2Corridor = Airport.runway2.getCorridor().getEntryWaypoint();

        Runway runway = null;

        if (plane.getLocation().equals(runway1Corridor)){
            return runway = Airport.runway1;
        }
        else if (plane.getLocation().equals(runway2Corridor)) {
            return runway = Airport.runway2;
        }
        return runway;
    }

    private void handleOutOfFuel(Plane plane) throws IOException {
        controller.removePlaneFromSpace(plane);
        log.info("Plane [{}] is out of fuel. Disappeared from the radar ", plane.getId());
    }

    private void handleLanding(Plane plane, Runway runway, ObjectInputStream in, ObjectOutputStream out) throws IOException, LocationAcquisitionException, ClassNotFoundException {
        out.writeObject(LAND);
        out.writeObject(runway);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getId(), runway.getId());

        while (true) {
            Object message = in.readObject();

            if(message instanceof String && message.equals("OUT_OF_FUEL")){
                break;
            }

            if(message instanceof Location){
                plane.setLocation((Location) message);
            }

            log.info("Plane [{}] is landing", plane.getId());

            if(controller.hasLandedOnRunway(plane, runway)){
                log.info("Plane [{}] has successfully landed on runway [{}]", plane.getId(), runway.getId());
                break;
            }

            if(controller.isRunwayCollision(plane)){
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
}