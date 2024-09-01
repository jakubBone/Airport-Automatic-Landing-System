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
        int airportSide = 5000;
        int side = Math.abs(airportSide - location.getY() * 2);
        int waypointDistance = side / 10;


        // Top side
        for (int x = -side; x <= side - waypointDistance; x += waypointDistance) {
            waypoints.add(new Waypoint(x, location.getY()));
        }

        // Right side
        for (int y = side; y >= - side + waypointDistance; y -= waypointDistance) {
            waypoints.add(new Waypoint(location.getX(), location.getY()));
        }

        // Bottom side
        for (int x = side; x >= - side + waypointDistance; x -= waypointDistance) {
            waypoints.add(new Waypoint(x, location.getY()));
        }

        // Bottom side
        for (int y = - side; y <= side - waypointDistance; y -= waypointDistance) {
            waypoints.add(new Waypoint(location.getX(), y));
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
