package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Location;

@Log4j2
@Getter
public class Runway {
    private String id;
    private Location location;
    public Runway(String id, Location location) {
        this.id = id;
        this.location = location;
    }
}
