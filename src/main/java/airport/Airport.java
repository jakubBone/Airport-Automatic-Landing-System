package airport;

import location.Location;
import lombok.Getter;


@Getter
public class Airport {
    public static final int MAX_CAPACITY = 100;
    public static Runway runway1;
    public static Runway runway2;

    public Airport() {
        this.runway1 = createRunway("R-1", "C-1", new Location(500, -1167, 0), new Location(-4861, -1167, 1000));
        this.runway2 = createRunway("R-1", "C-1", new Location(500, 1167, 0), new Location(-4861, 1167, 1000));
    }

    private Runway createRunway(String runwayId, String corridorId, Location landingPoint, Location entryPoint) {
        return new Runway(runwayId, landingPoint, new Corridor(corridorId, entryPoint));
    }
}
