package airport;

import lombok.Getter;
import utills.Constant;

@Getter
public class Airport {
    public static final int MAX_CAPACITY = 100;
    public static Runway runway2;
    public static Runway runway1;

    public Airport() {
        this.runway1 = RunwayFactory.create(
                "R-1",
                "C-1",
                Constant.LANDING_POINT_RUNWAY_1,
                Constant.ENTRY_POINT_CORRIDOR_1,
                Constant.FINAL_APPROACH_CORRIDOR_1
        );
        this.runway2 = RunwayFactory.create(
                "R-2",
                "C-2",
                Constant.LANDING_POINT_RUNWAY_2,
                Constant.ENTRY_POINT_CORRIDOR_2,
                Constant.FINAL_APPROACH_CORRIDOR_2
        );
    }
}
