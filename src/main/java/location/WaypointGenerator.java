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
    private static final int HOLDING_ALTITUDE = 2000;
    private static final int INNER_MAX_BOUNDARY = 4000;
    private static final int INNER_MIN_BOUNDARY = -4000;
    public  static final Location CORRIDOR_C1_WAYPOINT = new Location(-5000, 2000, 2000);
    public  static final Location CORRIDOR_C2_WAYPOINT = new Location(-5000, -2000, 2000);



   public static List<Location> getDescentWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        int[] altitudes = {5000, 4000, 3000};

        int altitudeLevel = 1000;
        int waypointsPerLevel = 40;


        int altitudePerWaypoint = altitudeLevel / waypointsPerLevel;

        for (int altitude : altitudes) {
            //  Top side
            for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL) {
                waypoints.add(new Location(x, MAX_AIRPORT_SIDE, altitude - altitudePerWaypoint));
                altitude -= altitudePerWaypoint;
            }

            // Right side
            for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL) {
                waypoints.add(new Location(MAX_AIRPORT_SIDE, y,altitude- altitudePerWaypoint));
                altitude -= altitudePerWaypoint;
            }

            // Left side
            for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL) {
                waypoints.add(new Location(x, MIN_AIRPORT_SIDE,altitude- altitudePerWaypoint));
                altitude -= altitudePerWaypoint;
            }

            // Bottom side
            for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL) {
                waypoints.add(new Location(MIN_AIRPORT_SIDE, y,altitude- altitudePerWaypoint));
                altitude -= altitudePerWaypoint;
            }

        }

        for(Location location: waypoints){
            System.out.println(waypoints.indexOf(location));
            System.out.println(location.getAltitude());
        }

        return waypoints;
    }

    public static List<Location> getHoldingPatternWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        // Waypoints on 2000m level
        for (int x = MIN_AIRPORT_SIDE; x <= MAX_AIRPORT_SIDE; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MAX_AIRPORT_SIDE, HOLDING_ALTITUDE));
        }
        for (int y = MAX_AIRPORT_SIDE; y >= MIN_AIRPORT_SIDE; y -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MAX_AIRPORT_SIDE, y, HOLDING_ALTITUDE));
        }
        for (int x = MAX_AIRPORT_SIDE; x >= MIN_AIRPORT_SIDE; x -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MIN_AIRPORT_SIDE, HOLDING_ALTITUDE));
        }
        for (int y = MIN_AIRPORT_SIDE; y <= MAX_AIRPORT_SIDE; y += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MIN_AIRPORT_SIDE, y, HOLDING_ALTITUDE));
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

