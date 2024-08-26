package plane;

import airport.Runway;
import location.Location;
import location.Waypoint;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int currentWaypointIndex;
    private int id;
    private double fuelLevel;
    private boolean hasLanded;
    private Location location;
    private List<Waypoint> waypoints;

    public Plane() {
        this.id = generateID();
        this.fuelLevel = 1000;
        this.waypoints = Waypoint.generateWaypoints();
        this.location = setInitialLocation();
    }

    public void holdPattern() {
        System.out.println("Current waypoint: " + currentWaypointIndex);
        Waypoint nextWaypoint = waypoints.get((currentWaypointIndex + 1) % waypoints.size());
        System.out.println("Next waypoint: " + nextWaypoint.getX() + " / " + nextWaypoint.getY());
        moveTowards(nextWaypoint);

        if(hasReachedWaypoint(nextWaypoint)){
            currentWaypointIndex = (currentWaypointIndex + 1) % waypoints.size();
        }

        fuelLevel -= 10;
    }

    public void directTowardsCorridor(Runway runway) {
        holdPattern();
        Waypoint corridorWaypoint = runway.getCorridor().getWaypoint();

        if(hasReachedWaypoint(corridorWaypoint)){
            return;
        }
        fuelLevel -= 10;
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
        Waypoint initialWaypoint = waypoints.get(random.nextInt(waypoints.size()));
        setCurrentWaypointIndex(initialWaypoint);
        int altitude = 2000 + random.nextInt(5000); // Altitude between 2000 and 5000 meters

        int x = initialWaypoint.getX();
        int y = initialWaypoint.getY();

        return new Location(x, y, altitude);
    }

    public void setCurrentWaypointIndex(Waypoint initialWaypoint) {
        currentWaypointIndex = waypoints.indexOf(initialWaypoint);
    }

    public void decreaseAltitude() {
        int newAltitude = getLocation().getAltitude() - 100;
        if (newAltitude < 0) {
            newAltitude = 0;
        }
        getLocation().setAltitude(newAltitude);
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

    public boolean hasLanded() {
        return location.getAltitude() == 0;
    }
}
