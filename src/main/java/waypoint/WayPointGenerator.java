package waypoint;
import java.util.ArrayList;
import java.util.List;

public class WayPointGenerator {
    public static List<Waypoint> generateWaypoints() {
        List<Waypoint> waypoints = new ArrayList<>();

        waypoints.add(new Waypoint(500, 0)); // E
        waypoints.add(new Waypoint(353, 353)); // NE
        waypoints.add(new Waypoint(0, 500)); // N
        waypoints.add(new Waypoint(-353, 353)); // NW
        waypoints.add(new Waypoint(-500, 0)); // W
        waypoints.add(new Waypoint(-353, -353)); // SW
        waypoints.add(new Waypoint(0, -500)); // S
        waypoints.add(new Waypoint(353, -353)); // SE

        /*
        waypoints.add(new Waypoint(5000, 0)); // E
        waypoints.add(new Waypoint(3535, 3535)); // NE
        waypoints.add(new Waypoint(0, 5000)); // N
        waypoints.add(new Waypoint(-3535, 3535)); // NW
        waypoints.add(new Waypoint(-5000, 0)); // W
        waypoints.add(new Waypoint(-3535, -3535)); // SW
        waypoints.add(new Waypoint(0, -5000)); // S
        waypoints.add(new Waypoint(3535, -3535)); // SE
         */
        return waypoints;
    }
}
