package airport;

import location.Location;
import lombok.Getter;


@Getter
public class Airport {
    public static final int MAX_CAPACITY = 100;
    public static Runway runway1;
    public static Runway runway2;

    public Airport() {
        this.runway1 = createRunwayWithCorridor("R-1", "C-1", new Location(1000, -2000, 0), new Location(-5000, -2000, 1000), true);
        this.runway2 = createRunwayWithCorridor("R-2", "C-2", new Location(1000, 1000, 0), new Location(-5000,  1000, 1000), true);
    }
    public Runway createRunwayWithCorridor(String runwayId, String corridorId, Location touchdownPoint, Location corridorEntryPoint, Boolean isAvailable){
        Corridor corridor = new Corridor(corridorId, corridorEntryPoint);
        return new Runway(runwayId, touchdownPoint, corridor, isAvailable);
    }
}
