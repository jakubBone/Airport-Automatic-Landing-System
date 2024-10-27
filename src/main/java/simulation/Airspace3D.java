package simulation;
import javafx.geometry.Point3D;
import location.Location;

public class Airspace3D  {
    public Point3D convertToDisplayScale(Location location) {
        double scale = 0.01; // Scale 1px = 100m
        double x = location.getX() * scale;
        double y = location.getY() * scale;
        double z = location.getAltitude() * scale;
        return new Point3D(x, y, z);
    }
}
