package airport;

import location.Location;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Location entryPoint;
    private Location finalApproachPoint;

    public Corridor(String id, Location entryPoint, Location finalApproachPoint) {
        this.id = id;
        this.entryPoint = entryPoint;
        this.finalApproachPoint = finalApproachPoint;
    }
}
