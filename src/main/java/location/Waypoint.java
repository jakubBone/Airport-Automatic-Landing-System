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
    public static List<Waypoint> generateCircleWaypoints(Location location) {
        List<Waypoint> waypoints = new ArrayList<>();
        int minCoord = -5000;
        int maxCoord = 5000;

        // example location (4500, 4800)

        int size = maxCoord - location.getY() * 2; // 4600
        int waypointLength = size / 10; // 46

        (4500, 4800)

        // Top side
        for (int x = location.getX(); x <= minCoord + location.getY(); x += waypointLength) {
            waypoints.add(new Waypoint(x, location.getY()));
        }

        return waypoints;
    }


    /*public static List<Waypoint> generateCircleWaypoints() {
        List<Waypoint> waypoints = new ArrayList<>();

        // 10 waypoints on each side, every 1000 meters
        // Every side length is 5000 meters

        // Top side
        for (int x = -5000; x <= 4000; x += 1000) {
            waypoints.add(new Waypoint(x, 5000));
        }

        // Right side
        for (int y = 5000; y >= -4000; y -= 1000) {
            waypoints.add(new Waypoint(5000, y));
        }

        // Bottom side
        for (int x = 5000; x >= -4000; x -= 1000) {
            waypoints.add(new Waypoint(x, -5000));
        }

        // Left side
        for (int y = -5000; y <= 4000; y += 1000) {
            waypoints.add(new Waypoint(-5000, y));
        }
        return waypoints;
    }*/

    public static List<Waypoint> getLandingWaypoints(int corridorY) {
        List<Waypoint> waypoints = new ArrayList<>();

        // 6 waypoints directing from corridor to runway, every 1000 meters
        for (int x = -4000; x <= 1000; x += 1000) {
            waypoints.add(new Waypoint(x, corridorY));
        }
        return waypoints;
    }
}
