package controller;

import airport.Airport;
import utills.Messenger;
import exceptions.LocationAcquisitionException;
import location.Location;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static controller.PlaneHandler.AirportInstruction.*;
import static plane.PlanePhase.FlightPhase.DESCENDING;

@Log4j2
public class PlaneHandler extends Thread {

    public enum AirportInstruction {
        DESCENT, HOLD_PATTERN, ALTERNATIVE, LAND, FULL, COLLISION, OCCUPIED
    }

    private final Socket clientSocket;
    private final AirTrafficController controller;
    private final Airport airport;
    private Messenger messenger;
    private FlightPhaseManager flightPhaseManager;

    public PlaneHandler(Socket clientSocket, AirTrafficController controller, Airport airport) {
        this.clientSocket = clientSocket;
        this.controller = controller;
        this.airport = airport;
        this.messenger = new Messenger();
        this.flightPhaseManager = new FlightPhaseManager(controller, airport, messenger);
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            handleClient(in, out);

        } catch (EOFException | SocketException ex) {
            log.warn("Connection to client lost. Client disconnected: {}", ex.getMessage());
        } catch (IOException | ClassNotFoundException | LocationAcquisitionException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                log.error("Failed to close client socket: {}", ex.getMessage(), ex);
            }
        }
    }

    public void handleClient(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, LocationAcquisitionException {
        Plane plane = messenger.receiveAndParse(in, Plane.class);

        if (!isPlaneRegistered(plane, out)) {
            return;
        }

        managePlane(plane, in, out);
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


        log.info("Plane [{}] registered in airspace on [{}] / [{}] / [{}]", plane.getId(), plane.getNavigator().getLocation().getX(), plane.getNavigator().getLocation().getY(), plane.getNavigator().getLocation().getAltitude());
        return true;
    }

    private void managePlane(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException, LocationAcquisitionException {
        /*plane.getPhase().setFlightPhase(DESCENDING);
        plane.setPhase(DESCENDING);*/
        plane.getPhase().setPhase(DESCENDING);

        while (true) {
            double fuelLevel = messenger.receiveAndParse(in, Double.class);
            plane.getFuelManager().setFuelLevel(fuelLevel);
            if (fuelLevel <= 0) {
                handleOutOfFuel(plane);
                return;
            }

            Location location = messenger.receiveAndParse(in, Location.class);
            flightPhaseManager.processFlightPhase(plane, location, in, out);

            if (plane.isDestroyed()) {
                handleCollision(plane, out);
                return;
            }

            if (plane.isLanded()) {
                log.info("Plane [{}] has successfully landed", plane.getId());
                return;
            }
        }
    }

    public void handleCollision(Plane plane, ObjectOutputStream out) throws IOException {
        log.info("COLLISION detected for Plane [{}]", plane.getId());
        controller.getPlanes().remove(plane);
        messenger.send(COLLISION, out);
    }

    private void handleOutOfFuel(Plane plane) throws IOException {
        plane.destroyPlane();
        controller.removePlaneFromSpace(plane);
        log.info("Plane [{}] is out of fuel. Disappeared from the radar ", plane.getId());
    }
}

