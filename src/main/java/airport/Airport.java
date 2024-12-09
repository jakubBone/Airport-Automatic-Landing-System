package airport;

import location.Location;
import lombok.Getter;


@Getter
public class Airport {
    public static final int MAX_CAPACITY = 1000;
    public static Runway runway2;
    public static Runway runway1;

    public Airport() {
        this.runway1 = createRunway("R-1", "C-1", new Location(3000, 1500, 0), new Location(-5000, 3500, 1000), new Location(-3000, 1500, 700)); // (-3000, 1000, 700)
        this.runway2 = createRunway("R-2", "C-2", new Location(3000, -1500, 0), new Location(-5000, 500, 1000), new Location(-3000, -1500, 700)); // (-3000, 1000, 700)
        //runway1.setAvailable(false);
        //runway2.setAvailable(false);
    }

    private Runway createRunway(String runwayId, String corridorId, Location landingPoint, Location entryPoint, Location secondEntryPoint) {
        return new Runway(runwayId, landingPoint, new Corridor(corridorId, entryPoint, secondEntryPoint));
    }
}
