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

    public static List<Location> generateCircularTrajectory(int centerX, int centerY, int radius, int altitude, int totalWaypoints) {
        List<Location> waypoints = new ArrayList<>();
        double angleStep = 360.0 / totalWaypoints; // Kąt między kolejnymi punktami

        for (int i = 0; i < totalWaypoints; i++) {
            double radians = Math.toRadians(i * angleStep);
            int x = centerX + (int) (radius * Math.cos(radians));
            int y = centerY + (int) (radius * Math.sin(radians));
            waypoints.add(new Location(x, y, altitude));
        }
        return waypoints;
    }

    public static List<Location> getDescentWaypoints() {
        List<Location> waypoints = new ArrayList<>();
        int radius = 5000; // Stały promień okręgu
        int totalWaypoints = 320; // Całkowita liczba waypointów
        int startAltitude = 5000; // Początkowa wysokość
        int endAltitude = 1000; // Końcowa wysokość
        double altitudeDecrement = (double) (startAltitude - endAltitude) / totalWaypoints; // Obniżenie na waypoint

        double angleStep = 360.0 / 80; // 80 waypointów na pełne okrążenie

        double currentAltitude = startAltitude;

        for (int i = 0; i < totalWaypoints; i++) {
            double radians = Math.toRadians((i % 80) * angleStep); // Powtarzanie kąta co 80 waypointów
            int x = (int) (radius * Math.cos(radians));
            int y = (int) (radius * Math.sin(radians));
            waypoints.add(new Location(x, y, (int) Math.round(currentAltitude)));

            currentAltitude -= altitudeDecrement; // Zmniejsz wysokość dla kolejnego waypointa
        }
        return waypoints;
    }

    public static List<Location> getHoldingPatternWaypoints() {
        int radius = 5000; // Promień okręgu
        int altitude = 1000; // Stała wysokość dla holding
        return generateCircularTrajectory(0, 0, radius, altitude, 80); // 80 waypointów
    }

    public static List<Location> getAlternativeHoldingPatternWaypoints() {
        return generateCircularTrajectory(0, 0, 5000, 500, 80); // 80 punktów
    }

    public static List<Location> getLandingWaypoints(Location corridorEntry) {
        List<Location> waypoints = new ArrayList<>();
        int startX = -4500;
        int endX = 500;
        int totalWaypoints = 10; // 10 waypointów
        int altitudeDecrement = corridorEntry.getAltitude() / totalWaypoints;
        int altitude = corridorEntry.getAltitude();

        for (int x = startX; x <= endX; x += (endX - startX) / totalWaypoints) {
            waypoints.add(new Location(x, corridorEntry.getY(), altitude));
            altitude -= altitudeDecrement;
        }
        return waypoints;
    }

    /*public static List<Location> getLandingWaypoints(Location corridorEntry) {
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
    }*/
}

