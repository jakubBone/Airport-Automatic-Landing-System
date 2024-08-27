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
    private int currentWaypointIndex;
    private int id;
    private double fuelLevel;
    private boolean landed;
    private Location location;
    private int interval;

    public Plane() {
        this.id = generateID();
        this.fuelLevel = 1000;
        this.location = setInitialLocation();
        this.currentPhase = HOLDING_PATTERN;
        this.interval = getAltitudeDecreaseInterval();
    }

    public int getAltitudeDecreaseInterval(){
        int corridorWaypointIndex = 37;
        int stepsToLandingAltitude = corridorWaypointIndex - currentWaypointIndex; // 6 index to różnica to 31
        int currentAltitude = location.getAltitude(); // 5000
        int landingAltitude = 2000;

        int altitudeDiff = currentAltitude - landingAltitude; // 3000
        return altitudeDiff / stepsToLandingAltitude; // 96
    }

    public void holdPattern() {
        if (currentPhase == HOLDING_PATTERN) {
            moveToNextWaypoint(Waypoint.getCircleWaypoints());
        }
        //decreaseAltitude();
        fuelLevel -= 10;
    }

    public void proceedToLand(Runway runway) {
        switch (currentPhase) {
            case HOLDING_PATTERN:
                moveToNextWaypoint(Waypoint.getCircleWaypoints());
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
        //decreaseAltitude();
        fuelLevel--;
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
        log.info("Current waypoint {}: [{}, {}]", currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY());
        log.info("Plane altitude {}: {}",id, location.getAltitude());
        moveTowards(nextWaypoint);

        if (hasReachedWaypoint(nextWaypoint)) {
            currentWaypointIndex++;
        }
        decreaseAltitude();
    }

    public void moveTowards(Waypoint nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
    }

    public boolean hasReachedWaypoint(Waypoint nextWaypoint) {
        return location.getX() == nextWaypoint.getX() && location.getY() == nextWaypoint.getY();
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location setInitialLocation() {
        Random random = new Random();
        List<Waypoint> circleWaypoints = Waypoint.getCircleWaypoints();
        currentWaypointIndex = random.nextInt(circleWaypoints.size());
        Waypoint initialWaypoint = circleWaypoints.get(currentWaypointIndex);

        return new Location(
                initialWaypoint.getX(),
                initialWaypoint.getY(),
                2000 + random.nextInt(3001) // Altitude between 2000 and 5000 meters
        );
    }

    /*public void decreaseAltitude() {
        int newAltitude = getLocation().getAltitude() - 100;
        if (newAltitude < 0) {
            getLocation().setAltitude(0);
        } else {
            getLocation().setAltitude(newAltitude);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Landing process interrupted for plane [{}]", getId());
        }
    }*/

    public void decreaseAltitude() {
        // wysokośc o jaką ma sie zmniejszyć to np 96
        int newAltitude = location.getAltitude() - interval;
        if (newAltitude < 0) {
            getLocation().setAltitude(0);
        } else {
            getLocation().setAltitude(newAltitude);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Landing process interrupted for plane [{}]", getId());
        }
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
