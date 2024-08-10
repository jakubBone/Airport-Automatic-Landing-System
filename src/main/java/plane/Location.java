package plane;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    double x;
    double y;
    double altitude;
    public Location(double x, double y, double altitude) {
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }
}