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
        Waypoint nextWaypoint;

        if(currentWaypointIndex + 1 >= waypoints.size()){
            nextWaypoint = waypoints.get(0);
        } else {
            nextWaypoint = waypoints.get(currentWaypointIndex + 1);
        }

        moveToNextWaypoint(nextWaypoint);

        if(hasReachedWaypoint(nextWaypoint)){
            currentWaypointIndex++;

            if(currentWaypointIndex >= waypoints.size()){
                currentWaypointIndex = 0;
            }
        }
        fuelLevel -= 10;
    }

    public void directTowardsCorridor(Runway runway){
        Waypoint nextWaypoint;

        int lastWaypointX = runway.getCorridor().getStartLocation().getX();
        int lastWaypointY = runway.getCorridor().getStartLocation().getY();

        Waypoint lastWaypoint = new Waypoint(lastWaypointX, lastWaypointY)

        if(currentWaypointIndex + 1 >= waypoints.size()){
            nextWaypoint = waypoints.get(0);
        } else {
            nextWaypoint = waypoints.get(currentWaypointIndex + 1);
        }

        moveToNextWaypoint(nextWaypoint);

        if(hasReachedWaypoint(nextWaypoint)){
            currentWaypointIndex++;

            if(isWaypointCorridor(nextWaypoint)){
                return;
            }

            if(currentWaypointIndex >= waypoints.size()){
                currentWaypointIndex = 0;
            }
        }
        fuelLevel -= 10;
    }

    public void moveToNextWaypoint(Waypoint nextWaypoint) {
        location.setX(nextWaypoint.getX());
        location.setY(nextWaypoint.getY());
    }


    public boolean hasReachedWaypoint(Waypoint nextWaypoint){
        return location.getX() == nextWaypoint.getX() && location.getY() == nextWaypoint.getY();
    }

    public boolean isWaypointCorridor(Waypoint nextWaypoint){
        return location.getX() == nextWaypoint.getX() && location.getY() == nextWaypoint.getY();
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location setInitialLocation(){
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
    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }

    public boolean hasLanded(){
        return location.getAltitude() == 0;
    }
}
