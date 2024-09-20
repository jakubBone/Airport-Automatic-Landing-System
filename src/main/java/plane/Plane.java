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
        LANDING
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
        //this.location = setInitialLocation();
        this.currentWaypointIndex = 118;
        this.location = waypoints.get(currentWaypointIndex);

        this.landed = false;
    }

    public void descend(){
        if (flightPhase == DESCENDING) {
            moveTowardsNextWaypoint();
            if (isAtLastWaypoint()) {
                flightPhase = FlightPhase.HOLDING;
                waypoints = WaypointGenerator.getHoldingPatternWaypoints();
                currentWaypointIndex = 0;
            }

        }
    }

    public void hold(){
        if (flightPhase == HOLDING) {
            moveTowardsNextWaypoint();
            if (isAtLastWaypoint()) {
                currentWaypointIndex = 0;
            }
        }
    }
    public void land(Runway runway){
        if (flightPhase == LANDING) {
            moveTowardsNextWaypoint();
            Location touchdownPoint = runway.getTouchdownPoint();
            log.info("Plane [{}] is LANDING on runway [{}]", getId(), runway.getId());
            if(hasReachedWaypoint(touchdownPoint)) {
                landed = true;
            }
        }
    }

    public void setLandingPhase(Runway runway) {
        this.flightPhase = FlightPhase.LANDING;
        this.waypoints = runway.getCorridor().getLandingPath();
        currentWaypointIndex = 0;
    }
    public void setHoldingPhase() {
        this.flightPhase = HOLDING;
        this.waypoints = WaypointGenerator.getHoldingPatternWaypoints();
        currentWaypointIndex = 0;
    }

    private void moveTowardsNextWaypoint() {
        if (currentWaypointIndex <= waypoints.size()) {
            Location nextWaypoint = waypoints.get(currentWaypointIndex);
            moveTowards(nextWaypoint);
            log.info("Plane [{}] is moving to waypoint {}: [{}, {}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY(), nextWaypoint.getAltitude());
            if (hasReachedWaypoint(nextWaypoint)) {
                currentWaypointIndex++;
                System.out.println("CURRENT WAYPOINT: " + currentWaypointIndex);
            }
        }
        burnFuel();
    }

    public boolean isAtLastWaypoint(){
        return currentWaypointIndex == waypoints.size();
    }

    public void moveTowards(Location nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
        location.setAltitude(nextWaypoint.getAltitude());
    }

    public boolean hasReachedWaypoint(Location waypoint) {
        return location.getX() == waypoint.getX() && location.getY() == waypoint.getY()
                && location.getAltitude() == waypoint.getAltitude();
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
                initialWaypoint.getAltitude()
        );
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
