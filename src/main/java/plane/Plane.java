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

import static plane.PlanePhase.FlightPhase.*;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int id;
    private boolean landed;
    private List <Location> waypoints;
    private boolean isDestroyed;
    private FuelManager fuelManager;
    private Navigator navigator;
    private PlanePhase phase;

    public Plane() {
        this.id = generateID();
        this.phase = new PlanePhase();
        this.fuelManager = new FuelManager();
        this.navigator = new Navigator(fuelManager);
        this.isDestroyed = false;
        this.landed = false;
    }

    public void descend(){
        navigator.moveTowardsNextWaypoint(id);
        navigator.setFirstMove(false);
        if (navigator.isAtLastWaypoint()) {
            phase.changePhase(HOLDING);
            navigator.setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
            navigator.setCurrentIndex(0);
        }
    }

    public void hold(){
        navigator.moveTowardsNextWaypoint(id);
        if (navigator.isAtLastWaypoint()) {
            navigator.setCurrentIndex(0);
        }
    }

    public void holdAlternative(){
        navigator.setWaypoints(WaypointGenerator.getAlternativeHoldingPatternWaypoints());
        navigator.moveTowardsNextWaypoint(id);
        if (navigator.isAtLastWaypoint()) {
            // Jump 1 index up at last point to avoid crash
            navigator.setCurrentIndex(1);
        }
    }

    public void land(Runway runway){
        navigator.moveTowardsNextWaypoint(id);
        log.info("Plane [{}] is LANDING on runway [{}]", getId(), runway.getId());
        if(navigator.isAtLastWaypoint()) {
            navigator.setLocation(runway.getLandingPoint());
            landed = true;
        }
    }

    public void setLandingPhase(Runway runway) {
        phase.changePhase(LANDING);
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
