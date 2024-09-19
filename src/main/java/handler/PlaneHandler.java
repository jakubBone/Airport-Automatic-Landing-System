package handler;

import airport.AirTrafficController;
import airport.Airport;
import airport.Runway;
import location.Location;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;

import static handler.PlaneHandler.Instruction.*;

@Log4j2
public class PlaneHandler extends Thread {

    public enum Instruction {
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

    private void handlePlaneMovement(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException {

        while (true) {
            // Getting actual position
            Location location = acquireLocation(in, plane);
            if (location == null) {
                controller.removePlaneFromSpace(plane);
                return;
            }
            plane.setLocation(location);

            // Check if plane reached a corridor
            if (isAtCorridorWaypoint(plane)) {
                if (controller.isAnyRunwayAvailable()) {
                    Runway runway = controller.getAvailableRunway();
                    plane.setLandingPhase(runway);
                    out.writeObject(LAND);
                    out.writeObject(runway);
                    break;
                } else {
                    out.writeObject(HOLD_PATTERN);
                }
            } else {
                out.writeObject(DESCENT);
            }
        }
    }

    private boolean isAtCorridorWaypoint(Plane plane) {
        return plane.hasReachedWaypoint(airport.getRunway1().getCorridor().getEntryWaypoint()) ||
                plane.hasReachedWaypoint(airport.getRunway2().getCorridor().getEntryWaypoint());
    }

    private void handleLanding(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        Runway runway = controller.getAvailableRunway();
        out.writeObject(LAND);
        out.writeObject(runway);

        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getId(), runway.getId());

        // Monitor landing
        while (true) {
            Location location = acquireLocation(in, plane);
            if (location == null) {
                if (plane.isLanded()) {
                    log.info("Plane [{}] has landed on runway [{}]", plane.getId(), runway.getId());
                } else {
                    log.error("Plane [{}] lost contact", plane.getId());
                }
                break;
            }
            plane.setLocation(location);
        }

        // Finalize landing
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
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error reading location for Plane [{}]: {}", plane.getId(), ex.getMessage());
            return null;
        }
    }
}