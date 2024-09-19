package plane;

import airport.Runway;
import location.Location;
import location.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static plane.Plane.FlightPhase.*;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {

    public enum FlightPhase {
        DESCENDING,
        HOLDING,
    }
    private FlightPhase flightPhase;
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int id;
    private double fuelConsumptionPerHour;
    private double fuelLevel;
    private boolean landed;
    private Location location;
    private List <Location> waypoints;
    private int currentWaypointIndex;

    public Plane() {
        this.id = generateID();
        this.fuelConsumptionPerHour = 2000;
        this.fuelLevel = calcFuelForThreeHours();
        this.flightPhase = DESCENDING;
        this.waypoints = WaypointGenerator.getDescentWaypoints();
        this.location = setInitialLocation();
        this.currentWaypointIndex = 0;
    }

    public void maintainFlight() {
        switch(flightPhase) {
            case DESCENDING:
                if (currentWaypointIndex >= waypoints.size()) {
                    flightPhase = HOLDING;
                    waypoints = WaypointGenerator.getHoldingPatternWaypoints()
                    currentWaypointIndex = 0;
                }
                break;
            case HOLDING:
                if (currentWaypointIndex >= waypoints.size()) {
                    currentWaypointIndex = 0;
                }
                break;
        }

        Location nextWaypoint = waypoints.get(currentWaypointIndex);
        moveTowards(nextWaypoint);
        log.info("Plane [{}] is moving to waypoint {}: [{}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY());

        if (hasReachedWaypoint(nextWaypoint)) {
            currentWaypointIndex++;
        }

        decreaseAltitude();
        log.info("Plane [{}] current altitude: {}", id, location.getAltitude());
        burnFuel();

    }


    public void land(Runway runway) {
        Location touchdownPoint = runway.getTouchdownPoint();
        waypoints = runway.getCorridor().getLandingPath();
        log.info("Plane [{}] is LANDING on runway [{}]", getId(), runway.getId());

        Location nextWaypoint = waypoints.get(currentWaypointIndex);
        moveTowards(nextWaypoint);

        log.info("Plane [{}] is moving to waypoint {}: [{}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY());

        if (hasReachedWaypoint(nextWaypoint)) {
            currentWaypointIndex++;
        }

        if (hasReachedWaypoint(touchdownPoint)) {
            landed = true;
        }

        burnFuel();
    }


    public void moveTowards(Location nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
    }

    public boolean hasReachedWaypoint(Location waypoint) {
        return waypoint.equals(location);
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location setInitialLocation() {
        Random random = new Random();

        List <Location> waypointsWithoutCorridors = waypoints;
        waypointsWithoutCorridors.remove(WaypointGenerator.CORRIDOR_C1_WAYPOINT);
        waypointsWithoutCorridors.remove(WaypointGenerator.CORRIDOR_C2_WAYPOINT);

        currentWaypointIndex = random.nextInt(waypointsWithoutCorridors.size());
        Location initialWaypoint = waypointsWithoutCorridors.get(currentWaypointIndex);

        return new Location(
                initialWaypoint.getX(),
                initialWaypoint.getY(),
                2000 + random.nextInt(3001) // Altitude between 2000 and 5000 meters
        );
    }

    public void decreaseAltitude() {
        if(flightPhase == DESCENDING && location.getAltitude()<= 2000){
            return;
        }

        int newAltitude;

        if(location.getAltitude() > 2000){
            newAltitude = location.getAltitude() - calcCircleAltitudeDecrease();
        } else {
            int defaultDecrease = 334;
            newAltitude = Math.max(location.getAltitude() - defaultDecrease, 0);
        }

        getLocation().setAltitude(newAltitude);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Landing process interrupted for Plane [{}]", getId());
        }
    }

    public int calcCircleAltitudeDecrease(){
        int corridorWaypointIndex = waypoints.size() - 1;
        int stepsToCorridorAltitude = corridorWaypointIndex - currentWaypointIndex;
        int currentAltitude = location.getAltitude();
        int corridorAltitude = 2000;

        int altitudeDiff = currentAltitude - corridorAltitude;
        return  altitudeDiff / stepsToCorridorAltitude;
    }

    public void burnFuel() {
        double fuelConsumptionPerSec = fuelConsumptionPerHour / 3600;
        fuelLevel -= fuelConsumptionPerSec;
    }

    public double calcFuelForThreeHours() {
        return (fuelConsumptionPerHour * 3);
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }

}
