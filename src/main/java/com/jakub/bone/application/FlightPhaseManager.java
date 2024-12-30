package com.jakub.bone.application;

import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.airport.Location;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.utills.Messenger;

import java.io.IOException;
import java.io.ObjectOutputStream;

import static com.jakub.bone.application.PlaneHandler.AirportInstruction.*;
import static com.jakub.bone.domain.plane.Plane.FlightPhase.*;
import static com.jakub.bone.utills.Constant.ENTRY_POINT_CORRIDOR_1;
import static com.jakub.bone.utills.Constant.ENTRY_POINT_CORRIDOR_2;

@Log4j2
public class FlightPhaseManager {
    private ControlTower controlTower;
    private Airport airport;
    private Messenger messenger;
    private Runway availableRunway;

    public FlightPhaseManager(ControlTower controlTower, Airport airport, Messenger messenger) {
        this.controlTower = controlTower;
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
        if (controlTower.isPlaneApproachingHoldingAltitude(plane)) {
            enterHolding(plane, out);
        } else {
            applyDescending(plane, out);
        }
    }

    private void handleHolding(Plane plane, ObjectOutputStream out) throws IOException {
        Runway runway = getRunwayIfPlaneAtCorridor(plane);
        availableRunway = runway;

        if(runway != null && controlTower.isRunwayAvailable(runway)){
            applyLanding(plane, runway, out);
        } else {
            applyHolding(plane, out);
        }
    }

    private void handleLanding(Plane plane) throws IOException, ClassNotFoundException {
        log.info("Plane [{}] is landing", plane.getFlightNumber());

        if (controlTower.hasLandedOnRunway(plane, availableRunway)) {
            plane.setLanded(true);
            try{
                Thread.sleep(500);
            } catch (InterruptedException ex){
                ex.getMessage();
            }
            controlTower.removePlaneFromSpace(plane);
            log.info("Plane [{}] has successfully landed on runway [{}]", plane.getFlightNumber(), availableRunway.getId());
            return;
        }
        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, availableRunway);
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
        controlTower.assignRunway(runway);
        plane.setPhase(LANDING);
        messenger.send(LAND, out);
        messenger.send(runway, out);
        log.info("Plane [{}] cleared for landing on runway [{}]", plane.getFlightNumber(), runway.getId());
    }

    private Runway getRunwayIfPlaneAtCorridor(Plane plane) {
        Runway runway;
        if (plane.getNavigator().getLocation().equals(ENTRY_POINT_CORRIDOR_1)){
            return runway = Airport.runway1;
        }
        else if (plane.getNavigator().getLocation().equals(ENTRY_POINT_CORRIDOR_2)) {
            return runway = Airport.runway2;
        }
        return null;
    }
}
