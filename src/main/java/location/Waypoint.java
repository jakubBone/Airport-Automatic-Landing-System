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

        waypoints.add(new Waypoint(5000, 0)); // E
        waypoints.add(new Waypoint(3535, 3535)); // NE
        waypoints.add(new Waypoint(0, 5000)); // N
        waypoints.add(new Waypoint(-3535, 3535)); // NW
        waypoints.add(new Waypoint(-5000, 0)); // W
        waypoints.add(new Waypoint(-3535, -3535)); // SW
        waypoints.add(new Waypoint(0, -5000)); // S
        waypoints.add(new Waypoint(3535, -3535)); // SE

        return waypoints;
    }

}
