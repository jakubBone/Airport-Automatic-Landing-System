package location;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WaypointGenerator implements Serializable {
    private static final int MAX_AIRPORT_SIDE = 5000;
    private static final int MIN_AIRPORT_SIDE = -5000;
    public static List<Location> generateCircleWaypoints(Location location) {
        List<Location> waypoints = new ArrayList<>();

        int distanceFromLeft = Math.abs(location.getX() - MIN_AIRPORT_SIDE);
        int distanceFromRight = Math.abs(MAX_AIRPORT_SIDE - location.getX());
        int distanceFromTop = Math.abs(MAX_AIRPORT_SIDE - location.getY());
        int distanceFromBottom = Math.abs(location.getY() - MIN_AIRPORT_SIDE);


        int minDistanceFromAirportSide = Math.min(Math.min(distanceFromLeft, distanceFromRight),
                Math.min(distanceFromTop, distanceFromBottom));

        int side = MAX_AIRPORT_SIDE - (minDistanceFromAirportSide * 2);

        // Round side to nearest 10
        side = (side * 10) / 10;

        // 10 waypoints per each side
        int waypointDistance = side / 10; // 480 bo (2000, 4900)

        int minSide = -side;
        int maxSide = side;


        // Top side
        for (int x = minSide; x <= maxSide - waypointDistance; x += waypointDistance) {
            waypoints.add(new Location(x, location.getY(), location.getAltitude()));
        }

        // Right side
        for (int y = maxSide; y >= minSide + waypointDistance; y -= waypointDistance) {
            waypoints.add(new Location(location.getX(), y, location.getAltitude()));
        }

        // Bottom side
        for (int x = maxSide; x >= minSide + waypointDistance; x -= waypointDistance) {
            waypoints.add(new Location(x, location.getY(), location.getAltitude()));
        }

        // Left side
        for (int y = minSide; y <= maxSide - waypointDistance; y += waypointDistance) {
            waypoints.add(new Location(location.getX(), y, location.getAltitude()));
        }

        return waypoints;
    }

    public static List<Location> getLandingWaypoints(int corridorY, int altitude) {
        List<WaypointGenerator> waypoints = new ArrayList<>();

        // 6 waypoints directing from corridor to runway, every 1000 meters
        for (int x = -4000; x <= 1000; x += 1000) {
            waypoints.add(new Location(x, corridorY, altitude - 1000));
        }
        return waypoints;
    }
}
