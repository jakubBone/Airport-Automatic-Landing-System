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
    public static List<Waypoint> setCircleWaypoints() {
        List<Waypoint> waypoints = new ArrayList<>();

        // There are 10 waypoints on each side of the square, every 1000 meters
        // Every side length is 5000 meters

        //Waypoints for the top side of the square
        for (int x = -5000; x <= 4000; x += 1000) {
            waypoints.add(new Waypoint(x, 5000)); // Top side (y = 5000)
        }

        // Waypoints for the right side of the square
        for (int y = 5000; y >= -4000; y -= 1000) {
            waypoints.add(new Waypoint(5000, y)); // Right side (y = 5000)
        }

        // Waypoints for the bottom side of the square
        for (int x = 5000; x >= -4000; x -= 1000) {
            waypoints.add(new Waypoint(x, -5000)); // Bottom side (y = -5000)
        }

        // Waypoints for the left side of the square
        for (int y = -5000; y <= 4000; y += 1000) {
            waypoints.add(new Waypoint(-5000, y)); // Left side (y = -5000)
        }
        return waypoints;
    }

    public static List<Waypoint> getLandingWaypoints(int corridorY) {
        List<Waypoint> waypoints = new ArrayList<>();

        // There are 7 waypoints directing throughout corridor to runway, every 1000 meters
        for (int x = -4000; x <= 1000; x += 1000) {
            waypoints.add(new Waypoint(x, corridorY));
        }
        return waypoints;
    }
}
