package plane;

import airport.Runway;
import location.Location;
import location.Waypoint;
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
        HOLDING_PATTERN,
        LANDING
    }
    private FlightPhase currentPhase;
    private static final AtomicInteger idCounter = new AtomicInteger();
    private List<Waypoint> circleWaypoints;
    private int currentWaypointIndex;
    private int id;
    private double fuelLevel;
    private boolean landed;
    private Location location;


    public Plane() {
        this.id = generateID();
        this.fuelLevel = 1000;
        this.currentPhase = HOLDING_PATTERN;
        this.circleWaypoints = Waypoint.setCircleWaypoints();
        this.location = setInitialLocation();
    }

    public void holdPattern() {
        if (currentPhase == HOLDING_PATTERN) {
            moveToNextWaypoint(circleWaypoints);
        }
        fuelLevel -= 10;
        log.info("Plane [{}] fuel level after holding pattern: {}", id, fuelLevel);
    }

    public void proceedToLand(Runway runway) {
        switch (currentPhase) {
            case HOLDING_PATTERN:
                moveToNextWaypoint(circleWaypoints);
                if (hasReachedWaypoint(runway.getCorridor().getWaypoint())) {
                    currentPhase = LANDING;
                    currentWaypointIndex = 0;
                    log.info("Plane [{}] is switching to LANDING phase", id);
                }
                break;
            case LANDING:
                List<Waypoint> landingWaypoints = runway.getCorridor().getLandingWay();
                moveToNextWaypoint(landingWaypoints);
                if (hasReachedWaypoint(runway.getWaypoint())) {
                    landed = true;
                }
                break;
        }
        fuelLevel--;
        log.info("Plane [{}] fuel level after landing attempt: {}", id, fuelLevel);
    }


    private void moveToNextWaypoint(List<Waypoint> waypoints) {
        if (currentWaypointIndex >= waypoints.size()) {
            if (currentPhase == FlightPhase.HOLDING_PATTERN) {
                currentWaypointIndex = 0;
            } else {
                return;
            }
        }

        Waypoint nextWaypoint = waypoints.get(currentWaypointIndex);
        log.info("Plane [{}] moving to waypoint {}: [{}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY());
        moveTowards(nextWaypoint);

        if (hasReachedWaypoint(nextWaypoint)) {
            currentWaypointIndex++;
        }
        decreaseAltitude();
    }

    public void moveTowards(Waypoint nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
        log.debug("Plane [{}] coordinates updated to [{}, {}]", id, location.getX(), location.getY());
    }

    public boolean hasReachedWaypoint(Waypoint nextWaypoint) {
        boolean reached = location.getX() == nextWaypoint.getX() && location.getY() == nextWaypoint.getY();
        log.debug("Plane [{}] checking if reached waypoint: {} Result: {}", id, nextWaypoint, reached);
        return reached;
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location setInitialLocation() {
        Random random = new Random();
        currentWaypointIndex = random.nextInt(circleWaypoints.size());
        Waypoint initialWaypoint = circleWaypoints.get(currentWaypointIndex);

        return new Location(
                initialWaypoint.getX(),
                initialWaypoint.getY(),
                2000 + random.nextInt(3001) // Altitude between 2000 and 5000 meters
        );
    }

    public void decreaseAltitude() {
        int newAltitude;

        if(location.getAltitude() > 2000){
            newAltitude = location.getAltitude() - calcCircleAltitudeDecrease();
        } else {
            int defaultDecrease = 334;
            newAltitude = Math.max(location.getAltitude() - defaultDecrease, 0);
        }

        getLocation().setAltitude(newAltitude);
        log.info("Plane [{}] altitude decreased to {}", id, newAltitude);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Landing process interrupted for Plane [{}]", getId());
        }
    }

    public int calcCircleAltitudeDecrease(){
        int corridorWaypointIndex = circleWaypoints.size() - 1;
        int stepsToCorridorAltitude = corridorWaypointIndex - currentWaypointIndex;
        int currentAltitude = location.getAltitude();
        int corridorAltitude = 2000;

        /*if (currentWaypointIndex >= corridorWaypointIndex){
            return 0;
        }*/

        int altitudeDiff = currentAltitude - corridorAltitude;
        return altitudeDiff / stepsToCorridorAltitude;
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
