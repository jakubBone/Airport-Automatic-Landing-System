package airport;

import location.Location;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Location entryWaypoint;

    public Corridor(String id, Location entryWaypoint) {
        this.id = id;
        this.entryWaypoint = entryWaypoint;
    }
}
