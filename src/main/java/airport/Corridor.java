package airport;

import location.Location;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Location entryPoint;

    public Corridor(String id, Location entryPoint) {
        this.id = id;
        this.entryPoint = entryPoint;
    }
}
