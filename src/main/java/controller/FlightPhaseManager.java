package controller;

import airport.Airport;
import airport.Runway;
import exceptions.LocationAcquisitionException;
import location.Location;
import lombok.extern.log4j.Log4j2;
import plane.Plane;
import utills.Messenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static controller.PlaneHandler.AirportInstruction.*;
import static plane.Plane.FlightPhase.*;

@Log4j2
public class FlightPhaseManager {

    private AirTrafficController controller;
    private Airport airport;
    private Messenger messenger;
    private Runway availableRunway;

    public FlightPhaseManager(AirTrafficController controller, Airport airport, Messenger messenger) {
        this.controller = controller;
        this.airport = airport;
        this.messenger = messenger;
    }

    public void processFlightPhase(Plane plane, Location location, ObjectInputStream in, ObjectOutputStream out) throws LocationAcquisitionException, IOException, ClassNotFoundException {
        plane.setLocation(location);
        switch (plane.getFlightPhase()) {
            case DESCENDING -> handleDescent(plane, out);
            case HOLDING -> handleHolding(plane, out);
            case ALTERNATIVE_HOLDING -> handleAlternativeHolding(plane, out);
            case LANDING -> handleLanding(plane);
            default -> log.warn("Unknown flight phase for Plane [{}]: {}", plane.getId(), plane.getFlightPhase());
        }
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        if (controller.isPlaneApproachingHoldingAltitude(plane)) {
            if (controller.isCollisionRisk(plane)) {
                applyAlternativeHolding(plane, out);
            } else {
                descentAndApplyHolding(plane, out);
            }
        } else {
            applyDescending(plane, out);
        }
    }

    private void handleAlternativeHolding(Plane plane, ObjectOutputStream out) throws IOException {
        if(controller.isPlaneLeavingAlternativeHolding(plane)) {
            if(controller.isCollisionRisk(plane)) {
                applyAlternativeHolding(plane, out);
            } else {
                descentAndApplyHolding(plane, out);
            }
        } else {
            applyAlternativeHolding(plane, out);
        }
    }

    private void handleHolding(Plane plane, ObjectOutputStream out) throws IOException {
        Runway runway = getRunwayIfPlaneAtCorridor(plane);
        availableRunway = runway;

        if(runway != null && controller.isRunwayAvailable(runway)){
            applyLanding(plane, runway, out);
        } else {
            applyHolding(plane, out);
        }
    }

    private void handleLanding(Plane plane) throws IOException, LocationAcquisitionException, ClassNotFoundException {
        log.info("Plane [{}] is landing", plane.getId());

        if (controller.hasLandedOnRunway(plane, availableRunway)) {
            plane.setLanded(true);
            completeLanding(plane, availableRunway);
            log.info("Plane [{}] has successfully landed on runway [{}]", plane.getId(), availableRunway.getId());
            return;
        }

        if (controller.isRunwayCollision(plane)) {
            completeLanding(plane, availableRunway);
            log.info("Runway collision detected for Plane [{}]:", plane.getId());
            return;
        }
    }

    private void applyDescending(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        plane.setFlightPhase(DESCENDING);
        log.info("Plane [{}] is descending", plane.getId());
    }

    private void descentAndApplyHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        plane.setFlightPhase(HOLDING);
        log.info("Plane [{}] is entering holding pattern", plane.getId());
    }

    private void applyHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(HOLD_PATTERN, out);
        plane.setFlightPhase(HOLDING);
        log.info("Plane [{}] is holding pattern", plane.getId());
    }

    private void applyAlternativeHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(ALTERNATIVE, out);
        plane.setFlightPhase(ALTERNATIVE_HOLDING);
        log.info("Plane [{}] is holding alternative pattern", plane.getId());
    }

    private void applyLanding(Plane plane, Runway runway, ObjectOutputStream out) throws IOException {
        controller.assignRunway(runway);
        plane.setFlightPhase(LANDING);
        messenger.send(LAND, out);
        messenger.send(runway, out);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getId(), runway.getId());
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

    private void completeLanding(Plane plane, Runway runway) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
        }

        controller.removePlaneFromSpace(plane);
        log.info("Plane [{}] removed from airspace", plane.getId());
        controller.releaseRunway(runway);
        log.info("Runway [{}] released", runway.getId());
    }
}
