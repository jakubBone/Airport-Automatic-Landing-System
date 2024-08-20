package location;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Waypoint implements Serializable {
    int x;
    int y;

    public Waypoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

        return waypoints;
    }
}
