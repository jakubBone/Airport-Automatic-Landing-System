package airport;

import location.Location;
import lombok.Getter;


@Getter
public class Airport {
    public static final int MAX_CAPACITY = 100;
    public static Runway runway2;
    public static Runway runway1;

    public Airport() {
        this.runway1 = createRunway("R-1", "C-1", new Location(500, 1000, 0), new Location(-5000, 3000, 1000), new Location(-3000, 1000, 700)); // (-3000, 1000, 700)
        this.runway2 = createRunway("R-2", "C-2", new Location(500, -2000, 0), new Location(-5000, 0, 1000), new Location(-3000, -2000, 700));  // (-3000, 1000, 700)
    }

    private Runway createRunway(String runwayId, String corridorId, Location landingPoint, Location entryPoint, Location secondEntryPoint) {
        return new Runway(runwayId, landingPoint, new Corridor(corridorId, entryPoint, secondEntryPoint));
    }
}
