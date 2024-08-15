package plane;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Location implements Serializable {
    int x;
    int y;
    int altitude;
    public Location(int x, int y, int altitude) {
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }
}