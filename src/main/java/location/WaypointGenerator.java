package location;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WaypointGenerator implements Serializable {
    private static final int MAX_AIRPORT_SIDE = 5000;
    private static final int MIN_AIRPORT_SIDE = -5000;
    private static final int WAYPOINT_INTERVAL_LAND = 500;
    private static final int LANDING_ALTITUDE = 1000;
    private static final int INNER_MAX_BOUNDARY = 4500;
    private static final int INNER_MIN_BOUNDARY = -4500;

    public static List<Location> getDescentWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        int[] altitudes = {5000, 4000, 3000, 2000};

        double altitudeLevel = 1000.0;
        int waypointsPerLevel = 80;

        // Precising decrement as double
        double altitudeDecrement = altitudeLevel / waypointsPerLevel;

        // 320 descending waypoints directing to landing level on 2000 meters
        for (int initialAltitude : altitudes) {
            double currentAltitude = initialAltitude;

            // Top side
            for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL_LAND) {
                waypoints.add(new Location(x, MAX_AIRPORT_SIDE, (int) Math.round(currentAltitude)));
                currentAltitude -= altitudeDecrement;
            }

            // Right side
            for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL_LAND) {
                waypoints.add(new Location(MAX_AIRPORT_SIDE, y, (int) Math.round(currentAltitude)));
                currentAltitude -= altitudeDecrement;
            }

            // Left side
            for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL_LAND) {
                waypoints.add(new Location(x, MIN_AIRPORT_SIDE, (int) Math.round(currentAltitude)));
                currentAltitude -= altitudeDecrement;
            }

            // Bottom side
            for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL_LAND) {
                waypoints.add(new Location(MIN_AIRPORT_SIDE, y, (int) Math.round(currentAltitude)));
                currentAltitude -= altitudeDecrement;
            }
        }

        return waypoints;
    }

    public static List<Location> getHoldingPatternWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        // 40 holding pattern waypoints on landing level
        for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(x, MAX_AIRPORT_SIDE, LANDING_ALTITUDE));
        }
        for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(MAX_AIRPORT_SIDE, y, LANDING_ALTITUDE));
        }
        for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(x, MIN_AIRPORT_SIDE, LANDING_ALTITUDE));
        }
        for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(MIN_AIRPORT_SIDE, y, LANDING_ALTITUDE));
        }

        return waypoints;
    }

    public static List<Location> getAlternativeHoldingPatternWaypoints() {
        List<Location> waypoints = new ArrayList<>();
        int alternativeAltitude = 500;

        // 40 alternative holding pattern waypoints
        for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(x, MAX_AIRPORT_SIDE, alternativeAltitude));
        }
        for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(MAX_AIRPORT_SIDE, y, alternativeAltitude));
        }
        for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(x, MIN_AIRPORT_SIDE, alternativeAltitude));
        }
        for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(MIN_AIRPORT_SIDE, y, alternativeAltitude));
        }

        return waypoints;
    }

    public static List<Location> getLandingWaypoints(Location corridorEntry) {
        List<Location> waypoints = new ArrayList<>();
        int startX = -4500;
        int endX = 500;
        int corridorY = corridorEntry.getY();

        int altitudeDecrement = LANDING_ALTITUDE / 10;
        int currentAltitude = corridorEntry.getAltitude();

        for (int x = startX; x <= endX; x += WAYPOINT_INTERVAL_LAND) {
            waypoints.add(new Location(x, corridorY, currentAltitude));
            currentAltitude -= altitudeDecrement;
        }

        return waypoints;
    }
}

