package plane;

import waypoint.WayPointGenerator;
import waypoint.Waypoint;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int currentWaypoint = 0;
    private int id;
    private double fuelLevel;
    private boolean hasLanded;
    private Location location;

    public Plane() {
        this.id = generateID();
        this.fuelLevel = 1000;
        this.location = new Location(10, 10, 10);
        this.location = generateRandomLocation();
    }

    public void holdPattern(){
        List<Waypoint> waypoints = WayPointGenerator.generateWaypoints();

        if (currentWaypoint == waypoints.size() -1) {
            currentWaypoint = 0; // return to 1st waypoint
        }
        Waypoint nextWaypoint = waypoints.get(currentWaypoint + 1);
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
        currentWaypoint++;
        fuelLevel -= 10;

    }
    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location generateRandomLocation(){
        return new Location(0, 499, 199);
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
