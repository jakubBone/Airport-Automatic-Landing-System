package airport;

import location.Location;
import lombok.Getter;


@Getter
public class AirSpace {
    public static final int MAX_CAPACITY = 100;
    private Runway runway1;
    private Runway runway2;

    public AirSpace() {
        this.runway1 = createRunwayWithCorridor("R-1", "C-1", new Location(1000, 2000, 0), new Location(-5000, 2000, 2000));
        this.runway2 = createRunwayWithCorridor("R-2", "C-2", new Location(1000, -2000, 0), new Location(-5000, -2000, 2000));
    }
    public Runway createRunwayWithCorridor(String runwayId, String corridorId, Location touchdownPoint, Location corridorEntryPoint){
        Corridor corridor = new Corridor(corridorId, corridorEntryPoint);
        return new Runway(runwayId, touchdownPoint, corridor);
    }
}
