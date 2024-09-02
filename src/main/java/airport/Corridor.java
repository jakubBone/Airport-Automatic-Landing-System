package airport;

import location.Location;
import location.WaypointGenerator;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Location corridorEntryPoint;
    private List<Location> landingPath;

    public Corridor(String id, Location entryPoint) {
        this.id = id;
        this.corridorEntryPoint = entryPoint;
        this.landingPath = WaypointGenerator.getLandingWaypoints(corridorEntryPoint);
    }
}
