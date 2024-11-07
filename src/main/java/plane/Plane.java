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
import java.util.stream.Collectors;

import static plane.Plane.FlightPhase.*;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {

    public enum FlightPhase {
        DESCENDING,
        HOLDING,
        LANDING,
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
    private boolean isDestroyed;
    private boolean isFirstMove;

    public Plane() {
        this.id = generateID();
        this.fuelConsumptionPerHour = 2000;
        this.fuelLevel = calcFuelForThreeHours();
        this.flightPhase = DESCENDING;
        this.waypoints = WaypointGenerator.getDescentWaypoints();
        this.location = selectInitialLocationExcludingCorridors();
        //this.currentWaypointIndex = 158;
        //this.location = waypoints.get(currentWaypointIndex);
        this.isDestroyed = false;
        this.landed = false;
        this.isFirstMove = true;
    }

    public void descend(){
        moveTowardsNextWaypoint();
        isFirstMove = false;
        if (isAtLastWaypoint()) {
            flightPhase = FlightPhase.HOLDING;
            waypoints = WaypointGenerator.getHoldingPatternWaypoints();
            currentWaypointIndex = 0;
        }
    }

    public void hold(){
        moveTowardsNextWaypoint();
        if (isAtLastWaypoint()) {
            currentWaypointIndex = 0;
        }
    }
    public void land(Runway runway){
        moveTowardsNextWaypoint();
        Location touchdownPoint = runway.getTouchdownPoint();
        log.info("Plane [{}] is LANDING on runway [{}]", getId(), runway.getId());

        if(isAtLastWaypoint()) {
            setLocation(touchdownPoint);
            landed = true;
        }
    }

    public void setLandingPhase(Runway runway) {
        this.flightPhase = FlightPhase.LANDING;
        Location entryPoint = runway.getCorridor().getEntryWaypoint();
        this.waypoints = WaypointGenerator.getLandingWaypoints(entryPoint);
        currentWaypointIndex = 0;
    }

    private void moveTowardsNextWaypoint() {
        if (currentWaypointIndex <= waypoints.size()) {
            Location nextWaypoint = waypoints.get(currentWaypointIndex);
            moveTowards(nextWaypoint);
            log.info("Plane [{}] is moving to waypoint {}: [{}, {}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY(), nextWaypoint.getAltitude());
            if (hasReachedWaypoint(nextWaypoint)) {
                currentWaypointIndex++;
            }
        }
    }

    public boolean isAtLastWaypoint(){
        return currentWaypointIndex == waypoints.size();
    }

    public void moveTowards(Location nextWaypoint) {
        if(!isFirstMove){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
        location.setAltitude(nextWaypoint.getAltitude());
        burnFuel();
    }

    public boolean hasReachedWaypoint(Location waypoint) {
        return location.getX() == waypoint.getX() && location.getY() == waypoint.getY()
                && location.getAltitude() == waypoint.getAltitude();
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location selectInitialLocationExcludingCorridors() {
        Random random = new Random();

        List<Location> waypointsToSpawn = waypoints.stream()
                .filter(wp -> wp.getAltitude() >= 2000 && wp.getAltitude() <= 5000)
                .collect(Collectors.toList());


        currentWaypointIndex = random.nextInt(waypointsToSpawn.size());
        Location initialWaypoint = waypointsToSpawn.get(currentWaypointIndex);

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
    public void destroyPlane() {
        this.isDestroyed = true;
    }

}
