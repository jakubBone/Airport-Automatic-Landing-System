package location;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WaypointGenerator implements Serializable {
    private static final int MAX_AIRPORT_SIDE = 5000;
    private static final int MIN_AIRPORT_SIDE = -5000;
    private static final int WAYPOINT_INTERVAL = 1000;
    private static final int ALTITUDE_START = 5000;
    private static final int ALTITUDE_END = 2000;
    public  static final Location CORRIDOR_C1_WAYPOINT = new Location(-5000, 2000, 2000);
    public  static final Location CORRIDOR_C2_WAYPOINT = new Location(-5000, -2000, 2000);


    public static List<Location> getDescentWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        // Waypoints between 5000 and 2000 meters
        for (int altitude  = ALTITUDE_START; altitude >= ALTITUDE_END; altitude += 100) {

            // Top side
            for (int x = MIN_AIRPORT_SIDE; x <= MAX_AIRPORT_SIDE; x += WAYPOINT_INTERVAL) {
                waypoints.add(new Location(x, MAX_AIRPORT_SIDE, altitude);
            }

            // Right side
            for (int y = MAX_AIRPORT_SIDE; y >= MIN_AIRPORT_SIDE; y -= WAYPOINT_INTERVAL) {
                waypoints.add(new Location(MAX_AIRPORT_SIDE, y, altitude);
            }

            // Left side
            for (int x = MAX_AIRPORT_SIDE; x >= MIN_AIRPORT_SIDE; x -= WAYPOINT_INTERVAL) {
                waypoints.add(new Location(x, MIN_AIRPORT_SIDE, altitude);
            }

            // Bottom side
            for (int y = MIN_AIRPORT_SIDE; y <= MAX_AIRPORT_SIDE; y += WAYPOINT_INTERVAL) {
                waypoints.add(new Location(MIN_AIRPORT_SIDE, y, altitude);
            }
        }
        return waypoints;
    }

    public static List<Location> getHoldingPatternWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        // Waypoints on 2000m level
        for (int x = MIN_AIRPORT_SIDE; x <= MAX_AIRPORT_SIDE; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MAX_AIRPORT_SIDE, ALTITUDE_END));
        }
        for (int y = MAX_AIRPORT_SIDE; y >= MIN_AIRPORT_SIDE; y -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MAX_AIRPORT_SIDE, y, ALTITUDE_END));
        }
        for (int x = MAX_AIRPORT_SIDE; x >= MIN_AIRPORT_SIDE; x -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MIN_AIRPORT_SIDE, ALTITUDE_END));
        }
        for (int y = MIN_AIRPORT_SIDE; y <= MAX_AIRPORT_SIDE; y += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MIN_AIRPORT_SIDE, y, ALTITUDE_END));
        }

        return waypoints;
    }

    public static List<Location> getLandingWaypoints(Location corridorEntryPoint) {
        List<Location> waypoints = new ArrayList<>();
        int corridorY = corridorEntryPoint.getY();
        int corridorAltitude = corridorEntryPoint.getAltitude();

        int firstCorridorX = - 4000;
        int lastCorridorY = 1000;

        // 6 waypoints directing to runway, every 1000 meters
        for (int x = firstCorridorX; x <= lastCorridorY; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, corridorY, corridorAltitude - WAYPOINT_INTERVAL));
        }

        return waypoints;
    }
}

