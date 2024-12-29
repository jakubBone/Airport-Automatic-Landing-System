package com.jakub.bone.client;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.application.PlaneHandler;
import lombok.Getter;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utills.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


@Log4j2
@Getter
public class PlaneInstructionHandler {
    private Plane plane;
    private Messenger messenger;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isProcessCompleted;
    private PlaneCommunicationService communicationService;

    public PlaneInstructionHandler(Plane plane, Messenger messenger, ObjectInputStream in, ObjectOutputStream out) {
        this.plane = plane;
        this.messenger = messenger;
        this.in = in;
        this.out = out;
        this.communicationService = new PlaneCommunicationService(plane, messenger, out);
    }

    public void processInstruction(PlaneHandler.AirportInstruction instruction) throws IOException, ClassNotFoundException {
        switch (instruction) {
            case DESCENT -> handleDescent();
            case HOLD_PATTERN -> handleHoldPattern();
            case LAND -> handleLanding();
            case COLLISION -> handleCollision();
            case FULL -> abortProcess("Airspace is FULL. Redirecting to another airport");
            case RISK_ZONE -> abortProcess("Initial location in RISK ZONE. Cannot proceed");
            default -> log.warn("Unknown instruction for Plane [{}]: [{}]", plane.getFlightNumber(), instruction);
        }
    }

    public void handleLanding() throws IOException, ClassNotFoundException {
        Runway runway = messenger.receiveAndParse(in, Runway.class);
        plane.setLandingPhase(runway);

        log.info("Plane [{}] assigned to LAND on runway {{}]", plane.getFlightNumber(), runway.getId());
        performLanding(runway);
    }

    private void performLanding(Runway runway) throws IOException {
        while (!isProcessCompleted) {

            if(!communicationService.sendFuelLevel()){
                return;
            }

            plane.land(runway);

            if(!communicationService.sendLocation()){
                return;
            }

            if (plane.isLanded()) {
                isProcessCompleted = true;
                log.info("Plane [{}] has successfully landed on runway {{}]", plane.getFlightNumber(), runway.getId());
            }
        }
    }

    private void handleDescent() {
        log.info("Plane [{}] instructed to DESCENT", plane.getFlightNumber());
        plane.descend();
    }

    private void handleHoldPattern() {
        log.info("Plane [{}] instructed to HOLD_PATTERN", plane.getFlightNumber());
        plane.hold();
    }

    private void abortProcess(String message) {
        log.info("Plane [{}]: {}", plane.getFlightNumber(), message);
        isProcessCompleted = true;
    }

    private void handleCollision() {
        log.info("COLLISION detected for Plane [{}]. Stopping communication", plane.getFlightNumber());
        plane.destroyPlane();
        isProcessCompleted = true;
    }
}
