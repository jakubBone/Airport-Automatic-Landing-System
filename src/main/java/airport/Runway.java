package airport;

import location.WaypointGenerator;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Getter
public class Runway implements Serializable {
    private String id;
    private WaypointGenerator waypoint;
    private Corridor corridor;

    public Runway(String id, WaypointGenerator waypoint, Corridor corridor) {
        this.id = id;
        this.waypoint = waypoint;
        this.corridor = corridor;
    }

}
