package handler;

import controller.AirTrafficController;
import airport.Airport;
import airport.Runway;
import communication.Messenger;
import exceptions.LocationAcquisitionException;
import location.Location;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static handler.PlaneHandler.AirportInstruction.*;

@Log4j2
public class PlaneHandler extends Thread {

    public enum AirportInstruction {
        DESCENT, HOLD_PATTERN, ALTERNATIVE,  LAND, FULL, COLLISION, OCCUPIED
    }
    private final Socket clientSocket;
    private final AirTrafficController controller;
    private final Airport airport;
    private Messenger messenger;

    public PlaneHandler(Socket clientSocket, AirTrafficController controller, Airport airport) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.airport = airport;
        this.messenger = new Messenger();
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String message = messenger.receive(in);
            Plane plane = messenger.parse(message, Plane.class);

            if (!isPlaneRegistered(plane, out)){
                return;
            }

            managePlane(plane, in, out);

        } catch (EOFException | SocketException ex) {
            log.warn("Connection to client lost. Client disconnected: {}", ex.getMessage());
        } catch (IOException | ClassNotFoundException | LocationAcquisitionException ex ) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                log.error("Failed to close client socket: {}", ex.getMessage(), ex);
            }
        }
    }

    private boolean isPlaneRegistered(Plane plane, ObjectOutputStream out) throws IOException {
        if (controller.isSpaceFull()) {
            messenger.send(FULL, out);
            log.info("No capacity in airspace for Plane [{}]", plane.getId());
            return false;
        }
        if (controller.isLocationOccupied(plane)) {
            messenger.send(OCCUPIED, out);
            log.info("Initial location Plane [{}] is occupied", plane.getId());
            return false;
        }
        controller.registerPlane(plane);
        log.info("Plane [{}] registered in airspace on [{}] / [{}] / [{}]", plane.getId(), plane.getLocation().getX(), plane.getLocation().getY(), plane.getLocation().getAltitude() );
        return true;
    }

    private void managePlane(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, LocationAcquisitionException {
        while (true) {
            // paliwo
            String message = messenger.receive(in);

            // Check if the message is numeric (fuel level) or not (Location)
            if (isNumeric(message)) {
                double fuelLevel = Double.parseDouble(message);
                plane.setFuelLevel(fuelLevel);
                if (fuelLevel <= 0) {
                    handleOutOfFuel(plane);
                    return;
                }
            }

            message = messenger.receive(in);
            Location location = messenger.parse(message, Location.class);
            plane.setLocation(location);

            if (plane.isDestroyed()) {
                log.info("COLLISION detected for Plane [{}]" , plane.getId());
                controller.getPlanes().remove(plane);
                messenger.send(COLLISION, out);
                return;
            }

            if (isPlaneAtLandingAltitude(plane)) {
                if (prepareLanding(plane, in, out)) {
                    break;
                }
            } else {
                handleDescent(plane, out);
            }
        }
    }

    private boolean prepareLanding(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, LocationAcquisitionException, ClassNotFoundException {
        Runway runway = getRunwayIfPlaneAtCorridor(plane);

        if(runway != null && controller.isRunwayAvailable(runway)){
            controller.assignRunway(runway);
            handleLanding(plane, runway, in, out);
            return true;
        }

        log.info("Plane [{}] is holding pattern", plane.getId());
        messenger.send(HOLD_PATTERN, out);
        return false;
    }

    private boolean isPlaneAtLandingAltitude(Plane plane) {
        return plane.getLocation().getAltitude() <= 1000;
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        log.info("Plane [{}] is descending", plane.getId());
    }

    private Runway getRunwayIfPlaneAtCorridor(Plane plane) {
        Location runway1Corridor = Airport.runway1.getCorridor().getEntryWaypoint();
        Location runway2Corridor = Airport.runway2.getCorridor().getEntryWaypoint();

        Runway runway;

        if (plane.getLocation().equals(runway1Corridor)){
            return runway = Airport.runway1;
        }
        else if (plane.getLocation().equals(runway2Corridor)) {
            return runway = Airport.runway2;
        }
        return null;
    }

    private void handleOutOfFuel(Plane plane) throws IOException {
        controller.removePlaneFromSpace(plane);
        log.info("Plane [{}] is out of fuel. Disappeared from the radar ", plane.getId());
    }

    private void handleLanding(Plane plane, Runway runway, ObjectInputStream in, ObjectOutputStream out) throws IOException, LocationAcquisitionException, ClassNotFoundException {
        messenger.send(LAND, out);
        messenger.send(runway, out);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getId(), runway.getId());

        while (true) {
            String message = messenger.receive(in);

            // Check if the message is numeric (fuel level)
            if (isNumeric(message)) {
                double fuelLevel = Double.parseDouble(message);
                plane.setFuelLevel(fuelLevel);
                if (fuelLevel <= 0) {
                    handleOutOfFuel(plane);
                    return;
                }
            }

            message = messenger.receive(in);
            Location location = messenger.parse(message, Location.class);
            plane.setLocation(location);

            log.info("Plane [{}] is landing", plane.getId());

            if(controller.hasLandedOnRunway(plane, runway)){
                plane.setLanded(true);
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            log.warn("PlaneHandler interrupted during sleep for completing landing. Thread was interrupted");
            Thread.currentThread().interrupt();
            return;
        }

        controller.removePlaneFromSpace(plane);
        log.info("Plane [{}] removed from airspace", plane.getId());

        controller.releaseRunway(runway);
        log.info("Runway [{}] released", runway.getId());
    }

    // Helper method to check if a string is numeric
    private boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }
}