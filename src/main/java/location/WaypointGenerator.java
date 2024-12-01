package location;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WaypointGenerator implements Serializable {

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
        int altitude = 1000;
        return generateHoldingWaypoints(altitude);
    }

    public static List<Location> getAlternativeHoldingPatternWaypoints() {
        int altitude = 500;
        return generateHoldingWaypoints(altitude);
    }

    public static List<Location> generateHoldingWaypoints(int altitude) {
        List<Location> waypoints = new ArrayList<>();
        // 1
        for (int y = 0; y <= 3000; y += 500) {
            waypoints.add(new Location(5000, y, altitude));
        }
        List<Location> arc = generateArc(3500, 3500, 1500, 0, 90, altitude);
        for(Location x: arc){
            waypoints.add(x);
        }

        // 2
        for (int x = 3000; x >= -3000; x -= 500) {
            waypoints.add(new Location(x, 5000, altitude));
        }
        List<Location> arc1 = generateArc(-3500, 3500, 1500, 90, 180, altitude);
        for(Location x: arc1){
            waypoints.add(x);
        }

        // 3
        for (int y = 3000; y >= -3000; y -= 500) {
            waypoints.add(new Location(-5000, y, altitude));
        }
        List<Location> arc3 = generateArc(-3500, -3500, 1500, 180, 270, altitude);
        for(Location x: arc3){
            waypoints.add(x);
        }

        // 4
        for (int x = -3000; x <= 3000; x += 500) {
            waypoints.add(new Location(x, -5000, altitude));
        }
        List<Location> arc4 = generateArc(3500, -3500, 1500, -90, 0, altitude);
        for(Location x: arc4){
            waypoints.add(x);
        }

        // 5
        for (int y = -3000; y <= -500; y += 500) {
            waypoints.add(new Location(5000, y, altitude));
        }

        return waypoints;
    }

    public static List<Location> generateArc(int centerX, int centerY, int radius, double startAngle, double endAngle, int altitude) {
        List<Location> arcPoints = new ArrayList<>();
        int waypoints = 4; // Liczba waypointów (początek, środek, koniec)

        double angleStep = (endAngle - startAngle) / (waypoints - 1);

        for (int i = 0; i < waypoints; i++) {
            double angle = Math.toRadians(startAngle + i * angleStep);
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            arcPoints.add(new Location(x, y, altitude));
        }

        return arcPoints;
    }

    /*public static List<Location> getAlternativeHoldingPatternWaypoints() {
        return generateCircularTrajectory(0, 0, 5000, 500, 80); // 80 punktów
    }*/

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

