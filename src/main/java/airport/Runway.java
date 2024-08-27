package airport;

import location.Waypoint;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import location.Location;

import java.io.Serializable;

@Log4j2
@Getter
public class Runway implements Serializable {
    private String id;
    private Waypoint waypoint;
    private Corridor corridor;

    public Runway(String id, Waypoint waypoint, Corridor corridor) {
        this.id = id;
        this.waypoint = waypoint;
        this.corridor = corridor;
    }

}
