package com.jakub.bone.application;

import com.jakub.bone.domain.airport.Airport;
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

@Log4j2
public class PlaneHandler extends Thread {
    public enum AirportInstruction {
        DESCENT, HOLD_PATTERN, LAND, FULL, COLLISION, RISK_ZONE
    }

    private final Socket clientSocket;
    private final ControlTower controlTower;
    private final Airport airport;
    private Messenger messenger;
    private FlightPhaseCoordinator phaseCoordinator;

    public PlaneHandler(Socket clientSocket, ControlTower controlTower, Airport airport) {
        this.clientSocket = clientSocket;
        this.controlTower = controlTower;
        this.airport = airport;
        this.messenger = new Messenger();
        this.phaseCoordinator = new FlightPhaseCoordinator(controlTower, airport, messenger);
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            handleClient(in, out);

        } catch (EOFException | SocketException ex) {
            log.warn("Connection to client lost. Client disconnected: {}", ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage(), ex);
        } finally {
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

        try{
            Thread.sleep(1000);
        } catch (InterruptedException ex){
            ex.getMessage();
        }

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
        if(plane.getAssignedRunway() != null){
            controlTower.releaseRunway(plane.getAssignedRunway());
        }
        controlTower.getPlanes().remove(plane);
        messenger.send(COLLISION, out);

        try{
            Thread.sleep(2000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
    }

    private void handleOutOfFuel(Plane plane) throws IOException {
        plane.destroyPlane();
        controlTower.removePlaneFromSpace(plane);
        log.info("Plane [{}]: out of fuel. Disappeared from the radar", plane.getFlightNumber());
    }
}

