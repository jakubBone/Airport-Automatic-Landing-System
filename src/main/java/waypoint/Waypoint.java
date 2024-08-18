package waypoint;

import lombok.Getter;

@Getter
public class Waypoint {
    int x;
    int y;

    public Waypoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
