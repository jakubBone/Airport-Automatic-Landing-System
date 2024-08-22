package airport;

import location.Location;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private Location startLocation;
    private Location endLocation;
    public Corridor(Location startLocation, Location endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }
}
