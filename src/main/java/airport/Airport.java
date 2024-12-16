package airport;

import location.Location;
import lombok.Getter;
import utills.Constant;


@Getter
public class Airport {
    public static final int MAX_CAPACITY = 1000;
    public static Runway runway2;
    public static Runway runway1;

    public Airport() {
        this.runway1 = createRunway("R-1", "C-1", Constant.LANDING_POINT_RUNWAY_1, Constant.ENTRY_POINT_CORRIDOR_1, Constant.SECOND_ENTRY_POINT_CORRIDOR_1);
        this.runway2 = createRunway("R-2", "C-2", Constant.LANDING_POINT_RUNWAY_2, Constant.ENTRY_POINT_CORRIDOR_2, Constant.SECOND_ENTRY_POINT_CORRIDOR_2);
    }

    private Runway createRunway(String runwayId, String corridorId, Location landingPoint, Location entryPoint, Location secondEntryPoint) {
        return new Runway(runwayId, landingPoint, new Corridor(corridorId, entryPoint, secondEntryPoint));
    }
}
