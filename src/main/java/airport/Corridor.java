package airport;

import location.Location;
import location.WaypointGenerator;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class Corridor implements Serializable {
    private String id;
    private WaypointGenerator waypoint;
    private List<Location> landingWay;

    public Corridor(String id, Location waypoint) {
        this.id = id;
        this.waypoint = waypoint;
        this.landingWay = WaypointGenerator.getLandingWaypoints(waypoint.getY());
    }
}
