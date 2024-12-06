package plane;

import airport.Runway;
import location.Location;
import location.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {
    public enum FlightPhase {
        DESCENDING,
        HOLDING,
        REDIRECTING,
        LANDING
    }
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int id;
    private boolean landed;
    private List <Location> waypoints;
    private boolean isDestroyed;
    private FuelManager fuelManager;
    private Navigator navigator;
    private FlightPhase phase;

    public Plane() {
        this.id = generateID();
        this.phase = FlightPhase.DESCENDING;
        this.fuelManager = new FuelManager();
        this.navigator = new Navigator(fuelManager);
        this.isDestroyed = false;
        this.landed = false;
    }

    public void descend(){
        navigator.move(id);
        if (navigator.isAtLastWaypoint()) {
            setPhase(FlightPhase.HOLDING);
            navigator.setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
            navigator.setCurrentIndex(0);
        }
    }

    public void hold(){
        navigator.move(id);
        if (navigator.isAtLastWaypoint()) {
            navigator.setCurrentIndex(0);
        }
    }

    public void holdAlternative(){
        navigator.setWaypoints(WaypointGenerator.getRedirectionWaypoints());
        navigator.move(id);
        if (navigator.isAtLastWaypoint()) {
            // Jump 1 index up at last point to avoid crash
            navigator.setCurrentIndex(1);
        }
    }

    public void land(Runway runway){
        navigator.move(id);
        log.info("Plane [{}] is LANDING on runway [{}]", getId(), runway.getId());
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

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public void destroyPlane() {
        this.isDestroyed = true;
    }
}
