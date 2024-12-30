package com.jakub.bone.domain.plane;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.utills.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {
    public enum FlightPhase {
        DESCENDING,
        HOLDING,
        LANDING
    }
    private String flightNumber;
    private boolean landed;
    private List <Location> waypoints;
    private boolean isDestroyed;
    private FuelManager fuelManager;
    private Navigator navigator;
    private FlightPhase phase;
    private Runway assignedRunway;

    public Plane(String flightNumber) {
        this.flightNumber = flightNumber;
        this.phase = FlightPhase.DESCENDING;
        this.fuelManager = new FuelManager();
        this.navigator = new Navigator(fuelManager);
        this.isDestroyed = false;
        this.landed = false;
        this.assignedRunway = null;
    }

    public void descend(){
        navigator.move(flightNumber);
        if (navigator.isAtLastWaypoint()) {
            setPhase(FlightPhase.HOLDING);
            navigator.setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
            navigator.setCurrentIndex(0);
        }
    }

    public void hold(){
        setPhase(FlightPhase.HOLDING);
        navigator.move(flightNumber);
        if (navigator.isAtLastWaypoint()) {
            navigator.setCurrentIndex(0);
        }
    }

    public void land(Runway runway){
        assignedRunway = runway;
        navigator.move(flightNumber);
        if(navigator.isAtLastWaypoint()) {
            navigator.setLocation(runway.getLandingPoint());
            landed = true;
        }
    }

    public void setLandingPhase(Runway runway) {
        setPhase(FlightPhase.LANDING);
        navigator.setWaypoints(WaypointGenerator.getLandingWaypoints(runway));
        navigator.setCurrentIndex(0);
    }

    public void destroyPlane() {
        this.isDestroyed = true;
    }
}
