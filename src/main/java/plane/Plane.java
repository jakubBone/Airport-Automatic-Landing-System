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
        //this.circleWaypoints = Waypoint.generateCircleWaypoints();
        this.location = getRandomLocation();
        this.circleWaypoints = WaypointGenerator.generateCircleWaypoints(location);
    }

    public void holdPattern() {
        if (currentPhase == HOLDING_PATTERN) {
            moveToNextWaypoint(circleWaypoints);
        }
        burnFuel();
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
                List<WaypointGenerator> landingWaypoints = runway.getCorridor().getLandingWay();
                moveToNextWaypoint(landingWaypoints);
                if (hasReachedWaypoint(runway.getWaypoint())) {
                    landed = true;
                }
                break;
        }
        burnFuel();
    }


    private void moveToNextWaypoint(List<WaypointGenerator> waypoints) {
        if (currentWaypointIndex >= waypoints.size()) {
            if (currentPhase == FlightPhase.HOLDING_PATTERN) {
                currentWaypointIndex = 0;
            } else {
                return;
            }
        }

        WaypointGenerator nextWaypoint = waypoints.get(currentWaypointIndex);
        log.info("Plane [{}] moving to waypoint {}: [{}, {}]", id, currentWaypointIndex, nextWaypoint.getX(), nextWaypoint.getY());
        moveTowards(nextWaypoint);

        if (hasReachedWaypoint(nextWaypoint)) {
            currentWaypointIndex++;
        }
        decreaseAltitude();
    }

    public void moveTowards(WaypointGenerator nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
        log.debug("Plane [{}] coordinates updated to [{}, {}]", id, location.getX(), location.getY());
    }

    public boolean hasReachedWaypoint(WaypointGenerator nextWaypoint) {
        boolean reached = location.getX() == nextWaypoint.getX() && location.getY() == nextWaypoint.getY();
        log.debug("Plane [{}] checking if reached waypoint: {} Result: {}", id, nextWaypoint, reached);
        return reached;
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    /*public Location setInitialLocation() {
        // Zakres współrzędnych kwadratu
        int minCoord = -5000;
        int maxCoord = 5000;

        // Szerokość pasów przylegających do krawędzi
        int borderWidth = 500;

        Random random = new Random();
        Location initLocation =



        currentWaypointIndex = random.nextInt(circleWaypoints.size());
        Waypoint initialWaypoint = circleWaypoints.get(currentWaypointIndex);

        return new Location(
                initialWaypoint.getX(),
                initialWaypoint.getY(),
                2000 + random.nextInt(3001) // Altitude between 2000 and 5000 meters
        );
    }*/

    public Location getRandomLocation() {
        // Zakres współrzędnych kwadratu
        int minCoo = -5000;
        int maxCoo = 5000;

        // Szerokość pasów przylegających do krawędzi
        int bufferWidth = 500;

        Random random = new Random();
        int side = random.nextInt(4);

        int x = 0;
        int y = 0;

        switch (side){
            case 0:// left
                x = minCoo + random.nextInt(bufferWidth + 1);
                y = minCoo + random.nextInt(maxCoo - minCoo + 1);
                break;
            case 1:// right
                x = maxCoo - random.nextInt(bufferWidth + 1);
                y = minCoo + random.nextInt(maxCoo - minCoo + 1);
                break;
            case 2:// top
                y = maxCoo - random.nextInt(bufferWidth + 1);
                x = minCoo + random.nextInt(maxCoo - minCoo + 1);
                break;
            case 3:// bottom
                y = minCoo + random.nextInt(bufferWidth + 1);
                x = minCoo + random.nextInt(maxCoo - minCoo + 1);
                break;
        }

        int minAltitude = 2000;
        int maxAltitude = 5000;


        int altitude = random.nextInt(maxAltitude - minAltitude + 1) + minAltitude;
        log.info("Plane [{}] initial localization: {} / {} / {}", id, x, y, altitude);

        return new Location(x, y, altitude);
    }

    /*public Location setInitialLocation() {
        Random random = new Random();
        currentWaypointIndex = random.nextInt(circleWaypoints.size());
        Waypoint initialWaypoint = circleWaypoints.get(currentWaypointIndex);

        return new Location(
                initialWaypoint.getX(),
                initialWaypoint.getY(),
                2000 + random.nextInt(3001) // Altitude between 2000 and 5000 meters
        );
    }*/

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

        int altitudeDiff = currentAltitude - corridorAltitude;
        return altitudeDiff / stepsToCorridorAltitude;
    }

    public void burnFuel() {
        double fuelConsumptionPerSec = fuelConsumptionPerHour / 3600;
        fuelLevel -= fuelConsumptionPerSec;
        log.info("Plane [{}] fuel level after burning: {}", id, String.format("%.2f", fuelLevel));
    }

    public double calcFuelForThreeHours() {
        return (fuelConsumptionPerHour * 3);
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
