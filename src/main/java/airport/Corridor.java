package airport;

import location.Location;
import location.WaypointGenerator;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Location entryWaypoint;
    private List<Location> landingPath;

    public Corridor(String id, Location entryWaypoint) {
        this.id = id;
        this.entryWaypoint = entryWaypoint;
        this.landingPath = WaypointGenerator.getLandingWaypoints(entryWaypoint);
    }
}
