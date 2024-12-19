package controller;

import airport.Airport;
import airport.Runway;
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

    public void processFlightPhase(Plane plane, Location location, ObjectOutputStream out) throws IOException, ClassNotFoundException {
        plane.getNavigator().setLocation(location);
        switch (plane.getPhase()) {
            case DESCENDING -> handleDescent(plane, out);
            case HOLDING -> handleHolding(plane, out);
            case LANDING -> handleLanding(plane);
            default -> log.warn("Unknown flight phase for Plane [{}]: {}", plane.getFlightNumber(), plane.getPhase());
        }
    }

    private void handleDescent(Plane plane, ObjectOutputStream out) throws IOException {
        if (controller.isPlaneApproachingHoldingAltitude(plane)) {
            enterHolding(plane, out);
        } else {
            applyDescending(plane, out);
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

    private void handleLanding(Plane plane) throws IOException, ClassNotFoundException {
        log.info("Plane [{}] is landing", plane.getFlightNumber());

        if (controller.hasLandedOnRunway(plane, availableRunway)) {
            plane.setLanded(true);
            try{
                Thread.sleep(500);
            } catch (InterruptedException ex){
                ex.getMessage();
            }
            controller.removePlaneFromSpace(plane);
            log.info("Plane [{}] has successfully landed on runway [{}]", plane.getFlightNumber(), availableRunway.getId());
            return;
        }
        controller.releaseRunwayIfPlaneAtFinalApproach(plane, availableRunway);
    }


    private void applyDescending(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        plane.setPhase(DESCENDING);
        log.info("Plane [{}] is descending", plane.getFlightNumber());
    }

    private void enterHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(DESCENT, out);
        plane.setPhase(HOLDING);
        log.info("Plane [{}] is entering holding pattern", plane.getFlightNumber());
    }

    private void applyHolding(Plane plane, ObjectOutputStream out) throws IOException {
        messenger.send(HOLD_PATTERN, out);
        plane.setPhase(HOLDING);
        log.info("Plane [{}] is holding pattern", plane.getFlightNumber());
    }

    private void applyLanding(Plane plane, Runway runway, ObjectOutputStream out) throws IOException {
        controller.assignRunway(runway);
        plane.setPhase(LANDING);
        messenger.send(LAND, out);
        messenger.send(runway, out);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getFlightNumber(), runway.getId());
    }

    private Runway getRunwayIfPlaneAtCorridor(Plane plane) {
        Location runway1Corridor = Airport.runway1.getCorridor().getEntryPoint();
        Location runway2Corridor = Airport.runway2.getCorridor().getEntryPoint();

        Runway runway;
        if (plane.getNavigator().getLocation().equals(runway1Corridor)){
            return runway = Airport.runway1;
        }
        else if (plane.getNavigator().getLocation().equals(runway2Corridor)) {
            return runway = Airport.runway2;
        }
        return null;
    }
}
