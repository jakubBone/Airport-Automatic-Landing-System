package plane;

import airport.Runway;
import location.Location;
import location.WaypointGenerator;
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
        STANDING_BY,
        LANDING
    }
    private String flightNumber;
    private boolean landed;
    private List <Location> waypoints;
    private boolean isDestroyed;
    private FuelManager fuelManager;
    private Navigator navigator;
    private FlightPhase phase;

    public Plane(String flightNumber) {
        this.flightNumber = flightNumber;
        this.phase = FlightPhase.DESCENDING;
        this.fuelManager = new FuelManager();
        this.navigator = new Navigator(fuelManager);
        this.isDestroyed = false;
        this.landed = false;
    }

    /*public Plane() {
        this.id = generateID();
        this.phase = FlightPhase.DESCENDING;
        this.fuelManager = new FuelManager();
        this.navigator = new Navigator(fuelManager);
        this.isDestroyed = false;
        this.landed = false;
    }*/

    public void descend(){
        navigator.move(flightNumber);
        if (navigator.isAtLastWaypoint()) {
            setPhase(FlightPhase.HOLDING);
            navigator.setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
            navigator.setCurrentIndex(0);
        }
    }

    public void hold(){
        navigator.move(flightNumber);
        if (navigator.isAtLastWaypoint()) {
            navigator.setCurrentIndex(0);
        }
    }

    public void standby(){
        navigator.setWaypoints(WaypointGenerator.getStandbyWaypoints());
        navigator.move(flightNumber);
        if (navigator.isAtLastWaypoint()) {
            // Jump 1 index up at last point to avoid crash
            navigator.setCurrentIndex(0);
        }
    }

    public void land(Runway runway){
        navigator.move(flightNumber);
        log.info("Plane [{}] is LANDING on runway [{}]", getFlightNumber(), runway.getId());
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
