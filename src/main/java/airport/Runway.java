package airport;

import location.Location;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Getter
@Setter
public class Runway implements Serializable {
    private String id;
    private Location touchdownPoint;
    private Corridor corridor;
    private boolean isAvailable;
    private double length;
    private double width;

    public Runway(String id, Location touchdownPoint, Corridor corridor, Boolean isAvailable) {
        this.id = id;
        this.touchdownPoint = touchdownPoint;
        this.corridor = corridor;
        this.isAvailable = isAvailable;
        this.length = 5000;
        this.width = 1000;
    }
}
