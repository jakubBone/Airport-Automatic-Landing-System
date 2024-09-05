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
        HOLDING_PATTERN,
        LANDING
    }
    private FlightPhase currentPhase;
    private static final AtomicInteger idCounter = new AtomicInteger();
    private List<Location> circleWaypoints;
    private int currentWaypointIndex;
    private int id;
    private double fuelConsumptionPerHour;
    private double fuelLevel;
    private boolean landed;
    private Location location;

    public Plane() {
        this.id = generateID();
        this.fuelConsumptionPerHour = 2000;
        this.fuelLevel = calcFuelForThreeHours();
        this.currentPhase = HOLDING_PATTERN;
        this.circleWaypoints = WaypointGenerator.generateCircleWaypoints();
        this.location = setInitialLocation();
    }

    public void holdPattern() {
        if (currentPhase == HOLDING_PATTERN) {
            moveToNextWaypoint(circleWaypoints);
        }
        burnFuel();
    }

    public void proceedToLand(Runway runway) {
        Location corridorEntryPoint = runway.getCorridor().getCorridorEntryPoint();
        Location touchdownPoint = runway.getTouchdownPoint();
        List<Location> landingPath = runway.getCorridor().getLandingPath();

        switch (currentPhase) {
            case HOLDING_PATTERN:
                moveToNextWaypoint(circleWaypoints);
                if (hasReachedWaypoint(corridorEntryPoint)) {
                    currentPhase = LANDING;
                    currentWaypointIndex = 0;
                    log.info("Plane [{}] is switching to LANDING phase", id);
                }
                break;
            case LANDING:
                log.info("Plane [{}] is LANDING on runway [{}]", getId(), runway.getId());
                moveToNextWaypoint(landingPath);
                if (hasReachedWaypoint(touchdownPoint)) {
                    landed = true;
                }
                break;
        }
        burnFuel();
    }

    private void moveToNextWaypoint(List<Location> waypoints) {
        if (currentWaypointIndex >= waypoints.size()) {
            if (currentPhase == FlightPhase.HOLDING_PATTERN) {
                currentWaypointIndex = 0;
            }
        }

        Location nextWaypoint = waypoints.get(currentWaypointIndex);
        log.info("Plane [{}] is moving to waypoint {}: [{}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY());
        moveTowards(nextWaypoint);

        if (hasReachedWaypoint(nextWaypoint)) {
            currentWaypointIndex++;
        }

        decreaseAltitude();
        log.info("Plane [{}] current altitude: {}", id, location.getAltitude());
    }

    public void moveTowards(Location nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
    }
    public boolean hasReachedWaypoint(Location nextWaypoint) {
        boolean reached = location.getX() == nextWaypoint.getX() && location.getY() == nextWaypoint.getY();
        return reached;
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location setInitialLocation() {
        Random random = new Random();
        currentWaypointIndex = random.nextInt(circleWaypoints.size());
        Location initialWaypoint = circleWaypoints.get(currentWaypointIndex);

        return new Location(
                initialWaypoint.getX(),
                initialWaypoint.getY(),
                2000 + random.nextInt(3001) // Altitude between 2000 and 5000 meters
        );
    }

    public void decreaseAltitude() {
        if(currentPhase == HOLDING_PATTERN && location.getAltitude()<= 2000){
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
    /*public void decreaseAltitude() {

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
    }*/

    public int calcCircleAltitudeDecrease(){
        int corridorWaypointIndex = circleWaypoints.size() - 1;
        int stepsToCorridorAltitude = corridorWaypointIndex - currentWaypointIndex;
        int currentAltitude = location.getAltitude();
        int corridorAltitude = 2000;

        int altitudeDiff = currentAltitude - corridorAltitude;
        return  altitudeDiff / stepsToCorridorAltitude;
    }

    public void burnFuel() {
        double fuelConsumptionPerSec = fuelConsumptionPerHour / 3600;
        fuelLevel -= fuelConsumptionPerSec;
        //log.info("Plane [{}] fuel level after burning: {}", id, String.format("%.2f", fuelLevel));
    }

    public double calcFuelForThreeHours() {
        return (fuelConsumptionPerHour * 3);
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
