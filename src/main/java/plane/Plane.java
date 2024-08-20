package plane;

import location.Location;
import location.Waypoint;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

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

    public void holdPattern(){
        if(currentWaypointIndex == 0){
            currentWaypointIndex++;
            return;
        }

        if (currentWaypointIndex == waypoints.size()) {
            currentWaypointIndex = 0; // return to 1st waypoint
        }

        Waypoint nextWaypoint = waypoints.get(currentWaypointIndex);
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
        currentWaypointIndex++;
        fuelLevel -= 10;
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location setInitialLocation(){
        Random random = new Random();
        Waypoint initialWaypoint = waypoints.get(random.nextInt(waypoints.size()));
        setCurrentWaypointIndex(initialWaypoint);
        int altitude = 200 + random.nextInt(499); // Altitude between 200 and 500 meters

        int x = initialWaypoint.getX();
        int y = initialWaypoint.getY();

        // Only add 500 if the coordinate is negative
        if (x < 0) {
            x += 500;
        }

        if (y < 0) {
            y += 500;
        }

        return new Location(x, y, altitude);
    }
    public void setCurrentWaypointIndex(Waypoint initialWaypoint) {
        currentWaypointIndex = waypoints.indexOf(initialWaypoint);
    }
    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
