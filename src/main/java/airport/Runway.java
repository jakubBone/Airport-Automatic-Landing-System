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
    private Location landingPoint;
    private Corridor corridor;
    private boolean isAvailable;
    private int width;
    private int height;

    public Runway(String id, Location landingPoint, Corridor corridor) {
        this.id = id;
        this.landingPoint = landingPoint;
        this.corridor = corridor;
        this.isAvailable = true;
        this.width = 5000;
        this.height = 1000;
    }
}
