package airport;

import location.Waypoint;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Waypoint waypoint;
    private List<Waypoint> landingWay;

    public Corridor(String id, Waypoint waypoint) {
        this.id = id;
        this.waypoint = waypoint;
        this.landingWay = Waypoint.getLandingWaypoints(waypoint.getY());
    }
}
