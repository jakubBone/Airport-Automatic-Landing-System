package location;

import airport.Runway;
import lombok.Getter;
import utills.Constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class WaypointGenerator implements Serializable {

    public static List<Location> getDescentWaypoints() {
       return generateDescentWaypoints();
    }

    public static List<Location> getHoldingPatternWaypoints() {
        return generateHoldingWaypoints(Constant.HOLDING_ALTITUDE);
    }

    public static List<Location> getStandbyWaypoints() {
        return generateHoldingWaypoints(Constant.STANDBY_ALTITUDE);
    }

    public static List<Location> getLandingWaypoints(Runway runway) {
        return generateLandingWaypoints(runway);
    }

    public static List<Location> generateDescentWaypoints() {
        List<Location> waypoints = new ArrayList<>();
        int radius = 5000;
        int totalWaypoints = 320;
        double altitudeDecrement = (double) (Constant.MAX_ALTITUDE - Constant.HOLDING_ALTITUDE) / totalWaypoints;

        double angleStep = 360.0 / 80;

        double currentAltitude = Constant.MAX_ALTITUDE;

        for (int i = 0; i < totalWaypoints; i++) {
            // Calculate the angle in radians for the current waypoint
            // (i % 80) angle resets after every 80 points, completing a full circle (360 degrees)
            // angleStep defines the angular interval between next points
            double radians = Math.toRadians((i % 80) * angleStep);
            int x = (int) (radius * Math.cos(radians));
            int y = (int) (radius * Math.sin(radians));
            waypoints.add(new Location(x, y, (int) Math.round(currentAltitude)));

            currentAltitude -= altitudeDecrement;
        }
        return waypoints;
    }

    public static List<Location> generateHoldingWaypoints(int altitude) {
        List<Location> waypoints = new ArrayList<>();
        int step = 500;

        for (int y = 0; y <= 3000; y += step) {
            waypoints.add(new Location(5000, y, altitude));
        }
        waypoints.addAll(generateArc(3500, 3500, 1500, 0, 90, altitude, 0));

        for (int x = 3000; x >= -3000; x -= step) {
            waypoints.add(new Location(x, 5000, altitude));
        }
        waypoints.addAll(generateArc(-3500, 3500, 1500, 90, 180, altitude, 0));

        for (int y = 3000; y >= -3000; y -= step) {
            waypoints.add(new Location(-5000, y, altitude));
        }
        waypoints.addAll(generateArc(-3500, -3500, 1500, 180, 270, altitude, 0));

        for (int x = -3000; x <= 3000; x += step) {
            waypoints.add(new Location(x, -5000, altitude));
        }
        waypoints.addAll(generateArc(3500, -3500, 1500, -90, 0, altitude, 0));

        for (int y = -3000; y <= -500; y += step) {
            waypoints.add(new Location(5000, y, altitude));
        }

        return waypoints;
    }

    public static List<Location> generateArc(int centerX, int centerY, int radius, double startAngle, double endAngle, int altitude, int altitudeDecrement) {
        List<Location> arcPoints = new ArrayList<>();
        int waypointsOnArc = 4;
        double angleStep = (endAngle - startAngle) / (waypointsOnArc - 1);

        for (int i = 0; i < waypointsOnArc; i++) {
            // Calculate the angle in radians for the current waypoint
            // startAngle is the initial angle of the arc, and angleStep defines the angular increment between points
            double angle = Math.toRadians(startAngle + i * angleStep);
            // Compute the X and Y coordinate using the formula for a point's position on a circle
            // centerX is the circle's center along the X-axis
            // centerY is the circle's center along the Y-axis
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            arcPoints.add(new Location(x, y, altitude));
            altitude -= altitudeDecrement;
        }

        return arcPoints;
    }

    public static List<Location> generateLandingWaypoints(Runway runway) {
        List<Location> waypoints = new ArrayList<>();
        int landingWaypoints = 10;
        int altitude = Constant.HOLDING_ALTITUDE;
        int altitudeDecrement = altitude / landingWaypoints;

        int arcCenterY = "R-2".equals(runway.getId()) ? 0 : 3000;
        List<Location> arc4 = generateArc(-3500, arcCenterY, 1500, 180, 270, altitude, altitudeDecrement);
        for(Location waypoint: arc4){
            waypoints.add(waypoint);
        }

        altitude = Constant.LANDING_ALTITUDE;
        int landingDescentY = runway.getCorridor().getEntryPoint().getY() - 2000;
        for (int x = -3000; x <= 500; x += 500) {
            waypoints.add(new Location(x, landingDescentY, altitude));
            altitude -= altitudeDecrement;
        }

        altitude = 0;
        // Add waypoints along the runway
        for (int x = 500; x <= 3000; x += 250) {
            waypoints.add(new Location(x, runway.getLandingPoint().getY(), altitude));
        }

        return waypoints;
    }
}

