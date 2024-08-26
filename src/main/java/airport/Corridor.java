package airport;

import location.Location;
import location.Waypoint;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private Location startLocation;
    private Location endLocation;
    private Waypoint waypoint;
    public Corridor(Location startLocation, Location endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.waypoint = getWaypoint();
    }
    public Waypoint getWaypoint(){
        int corridorWaypointX = startLocation.getX();
        int corridorWaypointY = startLocation.getY();
        return new Waypoint(corridorWaypointX, corridorWaypointY);
    }
}
