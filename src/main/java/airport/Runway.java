package airport;

import location.Location;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Getter
public class Runway implements Serializable {
    private String id;
    private Location touchdownPoint;
    private Corridor corridor;

    public Runway(String id, Location touchdownPoint, Corridor corridor) {
        this.id = id;
        this.touchdownPoint = touchdownPoint;
        this.corridor = corridor;
    }
}
