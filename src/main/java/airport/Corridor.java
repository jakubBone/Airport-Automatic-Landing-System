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

    public Corridor(String id, Location entryWaypoint) {
        this.id = id;
        this.entryWaypoint = entryWaypoint;
    }
}
