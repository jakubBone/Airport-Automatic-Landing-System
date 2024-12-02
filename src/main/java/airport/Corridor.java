package airport;

import location.Location;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class Corridor implements Serializable {
    private String id;
    private Location entryPoint;
    private Location secondEntryPoint;

    public Corridor(String id, Location entryPoint, Location secondEntryPoint) {
        this.id = id;
        this.entryPoint = entryPoint;
        this.secondEntryPoint = secondEntryPoint;
    }
}
