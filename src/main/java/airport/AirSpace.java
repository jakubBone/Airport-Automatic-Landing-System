package airport;

import lombok.Getter;
import plane.Plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AirSpace {
    private static final int MAX_CAPACITY = 100;
    private ArrayList<Plane> planesInSpace;
    private Map<String,Plane> hashSpace;

    public AirSpace() {
        this.planesInSpace = new ArrayList<>();
        this.hashSpace = new HashMap<>();
    }

    public String generateKay(int x, int y, int altitude){
        return x + ":" + y + ":" + altitude;
    }

    public void registerPlane(Plane plane){
        int x = plane.getLocation().getX();
        int y = plane.getLocation().getY();
        int altitude = plane.getLocation().getAltitude();

        String key = generateKay(x, y, altitude);

        planesInSpace.add(plane);
        hashSpace.put(key, plane);
    }

    public boolean isSpaceFull(){
        return planesInSpace.size() >= MAX_CAPACITY;
    }

    public void removePlaneFromSpace(Plane plane) {
        planesInSpace.remove(plane);
    }
}
