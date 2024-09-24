package handler;

import airport.AirTrafficController;
import airport.Airport;
import airport.Runway;
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
        FULL
    }

    private final Socket clientSocket;
    private final AirTrafficController controller;
    private final Airport airport;

    public PlaneHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.controller = new AirTrafficController();
        this.airport = new Airport();
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            Plane plane = (Plane) in.readObject();

            if (!registerPlane(plane, out)){
                return;
            }

            handlePlaneMovement(plane, in, out);

        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage());
        }
    }

    private boolean registerPlane(Plane plane, ObjectOutputStream out) throws IOException {
        if (controller.isSpaceFull()) {
            out.writeObject(FULL);
            log.info("No capacity in airspace for Plane [{}]", plane.getId());
            return false;
        }
        controller.registerPlane(plane);
        log.info("Plane [{}] registered in airspace", plane.getId());
        return true;
    }

    private void handlePlaneMovement(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException{
        while (true) {
            // Getting actual position
            Location location = acquireLocation(in, plane);
            if (location == null) {
                controller.removePlaneFromSpace(plane);
                return;
            }
            plane.setLocation(location);

            if (isPlaneAtLandingAltitude(plane)) {
                Runway runway = getRunwayIfPlaneInCorridor(plane);
                if(runway != null){
                    if(runway.isAvailable()){
                        controller.assignRunway(runway);
                        handleLanding(plane, runway, in, out);
                        break;
                    }
                }
                out.writeObject(HOLD_PATTERN);
            } else {
                out.writeObject(DESCENT);
            }
        }
    }

    private boolean isPlaneAtLandingAltitude(Plane plane) {
        return plane.getLocation().getAltitude() <= 2000;
    }

    private Runway getRunwayIfPlaneInCorridor(Plane plane) {
        Location corridor1Entry = airport.getRunway1().getCorridor().getEntryWaypoint();
        Location corridor2Entry = airport.getRunway2().getCorridor().getEntryWaypoint();

        int corridor1EntryX = corridor1Entry.getX();
        int corridor1EntryY = corridor1Entry.getY();

        int corridor2EntryX = corridor2Entry.getX();
        int corridor2EntryY = corridor2Entry.getY();

        int planeX = plane.getLocation().getX();
        int planeY = plane.getLocation().getY();

        Runway runway = null;

        if (planeX == corridor1EntryX && planeY == corridor1EntryY) {
            runway = airport.getRunway1();
        } else if (planeX == corridor2EntryX && planeY == corridor2EntryY) {
            runway = airport.getRunway2();
        }

        return runway;
    }

    private void handleLanding(Plane plane, Runway runway, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeObject(LAND);
        out.writeObject(runway);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getId(), runway.getId());

        while (true) {
            Object response = in.readObject();

            if (response instanceof Location) {
                Location location = (Location) response;
                plane.setLocation(location);
            } else if (response instanceof String && response.equals("LANDED")) {
                log.info("Plane [{}] has successfully landed on runway [{}]", plane.getId(), runway.getId());
                plane.setLanded(true);
                break;
            } else {
                log.error("Unexpected response for Plane [{}]: {}", plane.getId(), response);
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

    public Location acquireLocation(ObjectInputStream in, Plane plane) {
        try {
            return (Location) in.readObject();
        } catch (IOException | ClassNotFoundException  | NullPointerException ex) {
            log.error("Error reading location for Plane [{}]: {}", plane.getId(), ex.getMessage());
            return null;
        }
    }
}