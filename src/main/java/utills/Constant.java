package utills;

import location.Location;

public class Constant {

    // LANDING
    public static int LANDING_ALTITUDE = 700;
    public  static Location LANDING_POINT_RUNWAY_1 = new Location(3000, 1500, 0);
    public static Location LANDING_POINT_RUNWAY_2 = new Location(3000, -1500, 0);
    public static Location ENTRY_POINT_CORRIDOR_1 = new Location(-5000, 3500, 1000);
    public static Location ENTRY_POINT_CORRIDOR_2 = new Location(-5000, 500, 1000);
    public static Location FINAL_APPROACH_CORRIDOR_1 = new Location(-3000, 1500, 700);
    public static Location FINAL_APPROACH_CORRIDOR_2 = new Location(-3000, -1500, 700);

    // HOLDING
    public static int HOLDING_ALTITUDE = 1000;
    public static int HOLDING_ENTRY_ALTITUDE = 1013;


    // DESCENDING
    public static int MAX_ALTITUDE = 5000;
}
