package airport;

import location.Location;

import java.io.Serializable;

public class Corridor implements Serializable {
    private Location startLocation;
    private Location endLocation;
    public Corridor(Location startLocation, Location endLocation) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }
}
