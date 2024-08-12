package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Location;

import java.io.Serializable;

@Log4j2
@Getter
public class Runway implements Serializable {
    private String id;
    private Location location;
    public Runway(String id, Location location) {
        this.id = id;
        this.location = location;
    }
}
