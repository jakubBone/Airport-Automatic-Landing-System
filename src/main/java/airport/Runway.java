package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import location.Location;

import java.io.Serializable;

@Log4j2
@Getter
public class Runway implements Serializable {
    private String id;
    private Location startPoint;
    private Location endPoint;
    private Corridor corridor;
    public Runway(String id, Location startPoint, Location endPoint,  Corridor corridor) {
        this.id = id;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.corridor = corridor;
    }

}
