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
    private static final int INNER_MAX_BOUNDARY = 4000;
    private static final int INNER_MIN_BOUNDARY = -4000;


    public static List<Location> generateCircleWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        // 10 waypoints on each side, every 10 km

        //  Top side
        for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MAX_AIRPORT_SIDE, 0));
        }

        // Right side
        for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MAX_AIRPORT_SIDE, y,0));
        }

        // Left side
        for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MIN_AIRPORT_SIDE,0));
        }

        // Bottom side
        for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MIN_AIRPORT_SIDE, y,0));
        }

        return waypoints;
    }

    public static List<Location> getLandingWaypoints(Location corridorEntryPoint) {
        List<Location> waypoints = new ArrayList<>();
        int corridorY = corridorEntryPoint.getY();
        int corridorAltitude = corridorEntryPoint.getAltitude();

        // 6 waypoints directing to runway, every 1000 meters
        for (int x = INNER_MIN_BOUNDARY; x <= WAYPOINT_INTERVAL; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, corridorY, corridorAltitude - WAYPOINT_INTERVAL));
        }

        return waypoints;
    }

}
