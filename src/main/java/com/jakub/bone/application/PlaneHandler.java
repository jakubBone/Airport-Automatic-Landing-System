package com.jakub.bone.application;

import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.service.FlightPhaseService;
import com.jakub.bone.utills.Messenger;
import com.jakub.bone.domain.airport.Location;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.domain.plane.Plane;
import org.apache.logging.log4j.ThreadContext;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static com.jakub.bone.application.PlaneHandler.AirportInstruction.*;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.DESCENDING;
import static com.jakub.bone.config.Constant.*;

@Log4j2
public class PlaneHandler extends Thread {
    public enum AirportInstruction {
        DESCENT, HOLD_PATTERN, LAND, FULL, COLLISION, RISK_ZONE
    }
    private final Socket clientSocket;
    private final ControlTowerService controlTower;
    private final Airport airport;
    private Messenger messenger;
    private FlightPhaseService phaseCoordinator;

    public PlaneHandler(Socket clientSocket, ControlTowerService controlTower, Airport airport) {
        this.clientSocket = clientSocket;
        this.controlTower = controlTower;
        this.airport = airport;
        this.messenger = new Messenger();
        this.phaseCoordinator = new FlightPhaseService(controlTower, airport, messenger);
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            handleClient(in, out);
        } catch (EOFException | SocketException ex) {
            log.warn("Connection to client lost. Client disconnected: {}", ex.getMessage(), ex);
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
            closeResources(in, out);
            try {
                clientSocket.close();
            } catch (IOException ex) {
                log.error("Failed to close client socket: {}", ex.getMessage(), ex);
            }
        }
    }

    private void handleClient(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        Plane plane = messenger.receiveAndParse(in, Plane.class);

        if (!isPlaneRegistered(plane, out)) {
            return;
        }

        managePlane(plane, in, out);
    }

    private boolean isPlaneRegistered(Plane plane, ObjectOutputStream out) throws IOException {
        if (controlTower.isSpaceFull()) {
            messenger.send(FULL, out);
            log.info("Plane [{}]: no capacity in airspace", plane.getFlightNumber());
            return false;
        }

        waitForUpdate(UPDATE_DELAY);

        if (controlTower.isAtCollisionRiskZone(plane)) {
            messenger.send(RISK_ZONE, out);
            log.info("Plane [{}]: initial location occupied. Redirecting", plane.getFlightNumber());
            return false;
        }
        controlTower.registerPlane(plane);

        log.info("Plane [{}]: registered at ({}, {}, {}) ", plane.getFlightNumber(), plane.getNavigator().getLocation().getX(), plane.getNavigator().getLocation().getY(), plane.getNavigator().getLocation().getAltitude());
        return true;
    }

    private void managePlane(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        plane.setPhase(DESCENDING);

        while (true) {
            double fuelLevel = messenger.receiveAndParse(in, Double.class);
            plane.getFuelManager().setFuelLevel(fuelLevel);

            if (fuelLevel <= 0) {
                handleOutOfFuel(plane);
                return;
            }

            Location location = messenger.receiveAndParse(in, Location.class);
            phaseCoordinator.processFlightPhase(plane, location, out);

            if (plane.isDestroyed()) {
                handleCollision(plane, out);
                return;
            }

            if (plane.isLanded()) {
                log.info("Plane [{}]: successfully landed", plane.getFlightNumber());
                return;
            }
        }
    }

    private void handleCollision(Plane plane, ObjectOutputStream out) throws IOException {
        if (plane.getAssignedRunway() != null) {
            controlTower.releaseRunway(plane.getAssignedRunway());
        }
        controlTower.getPlanes().remove(plane);
        messenger.send(COLLISION, out);

        waitForUpdate(AFTER_COLLISION_DELAY);
    }

    private void handleOutOfFuel(Plane plane) throws IOException {
        plane.destroyPlane();
        controlTower.removePlaneFromSpace(plane);
        log.info("Plane [{}]: out of fuel. Disappeared from the radar", plane.getFlightNumber());
    }

    private void waitForUpdate(int interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ex) {
            log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
    }

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    log.error("Failed to close resource: {}", ex.getMessage(), ex);
                }
            }
        }
    }
}


