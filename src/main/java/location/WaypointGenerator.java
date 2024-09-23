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
    private static final int LANDING_ALTITUDE = 2000;
    private static final int INNER_MAX_BOUNDARY = 4000;
    private static final int INNER_MIN_BOUNDARY = -4000;
    public  static final Location CORRIDOR_C1_WAYPOINT = new Location(-5000, 2000, 2000);
    public  static final Location CORRIDOR_C2_WAYPOINT = new Location(-5000, -2000, 2000);



   public static List<Location> getDescentWaypoints() {
        List<Location> waypoints = new ArrayList<>();

        int[] altitudes = {5000, 4000, 3000};

        int altitudeLevel = 1000;
        int waypointsPerLevel = 40;

        int altitudeDecrement = altitudeLevel / waypointsPerLevel;

        for (int altitude : altitudes) {
            //  Top side
            for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL) {
                waypoints.add(new Location(x, MAX_AIRPORT_SIDE, altitude - altitudeDecrement));
                altitude -= altitudeDecrement;
            }

            // Right side
            for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL) {
                waypoints.add(new Location(MAX_AIRPORT_SIDE, y,altitude- altitudeDecrement));
                altitude -= altitudeDecrement;
            }

            // Left side
            for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL) {
                waypoints.add(new Location(x, MIN_AIRPORT_SIDE,altitude- altitudeDecrement));
                altitude -= altitudeDecrement;
            }

            // Bottom side
            for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL) {
                waypoints.add(new Location(MIN_AIRPORT_SIDE, y,altitude- altitudeDecrement));
                altitude -= altitudeDecrement;
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
        for (int x = MIN_AIRPORT_SIDE; x <= INNER_MAX_BOUNDARY; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MAX_AIRPORT_SIDE, LANDING_ALTITUDE));
        }
        for (int y = MAX_AIRPORT_SIDE; y >= INNER_MIN_BOUNDARY; y -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MAX_AIRPORT_SIDE, y, LANDING_ALTITUDE));
        }
        for (int x = MAX_AIRPORT_SIDE; x >= INNER_MIN_BOUNDARY; x -= WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, MIN_AIRPORT_SIDE, LANDING_ALTITUDE));
        }
        for (int y = MIN_AIRPORT_SIDE; y <= INNER_MAX_BOUNDARY; y += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(MIN_AIRPORT_SIDE, y, LANDING_ALTITUDE));
        }

        return waypoints;
    }

    public static List<Location> getLandingWaypoints(Location corridorEntry) {
        List<Location> waypoints = new ArrayList<>();
        int startX = -4000;
        int endX = 1000;
        int corridorY = corridorEntry.getY();

        int altitudeDecrement = LANDING_ALTITUDE / 5;
        int currentAltitude = corridorEntry.getAltitude();

        // 6 waypoints directing to runway, every 1000 meters
        for (int x = startX; x <= endX; x += WAYPOINT_INTERVAL) {
            waypoints.add(new Location(x, corridorY, currentAltitude));
            currentAltitude -= altitudeDecrement;
        }

        return waypoints;
    }

}

