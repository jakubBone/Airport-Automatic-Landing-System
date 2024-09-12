package airport;

import lombok.Getter;
import plane.Plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AirSpace {
    private static final int MAX_CAPACITY = 100;
    private ArrayList<Plane> planes;
    private Map<String,Plane> space;

    public AirSpace() {
        this.planes = new ArrayList<>();
        this.space = new HashMap<>();
    }

    public String generateKey(int x, int y, int altitude){
        return x + ":" + y + ":" + altitude;
    }

    public void registerPlane(Plane plane){
        int x = plane.getLocation().getX();
        int y = plane.getLocation().getY();
        int altitude = plane.getLocation().getAltitude();

        String key = generateKey(x, y, altitude);

        planes.add(plane);

        space.put(key, plane);
    }

    public boolean isSpaceFull(){
        return planes.size() >= MAX_CAPACITY;
    }

    public void removePlaneFromSpace(Plane plane) {
        planes.remove(plane);
    }
}
