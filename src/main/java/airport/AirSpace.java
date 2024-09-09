package airport;

import lombok.Getter;
import plane.Plane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class AirSpace {
    private static final int MAX_CAPACITY = 100;
    private ArrayList<Plane> planes;
    private Map<String,Plane> space;

    private Lock lock;

    public AirSpace() {
        this.planes = new ArrayList<>();
        this.space = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public String generateKay(int x, int y, int altitude){
        return x + ":" + y + ":" + altitude;
    }

    public void registerPlane(Plane plane){
        int x = plane.getLocation().getX();
        int y = plane.getLocation().getY();
        int altitude = plane.getLocation().getAltitude();

        String key = generateKay(x, y, altitude);

        planes.add(plane);
        space.put(key, plane);
    }

    // Lowest plane is search
    // After assign a runway plane needs to be release from sorting to find another lowest
    public boolean isPlaneLowest(Plane plane){
        lock.lock();
        try {
            Collections.sort(planes, (p1, p2) -> Integer.compare(p1.getLocation().getAltitude(), p2.getLocation().getAltitude()));
            return plane.getLocation().getAltitude() == planes.get(0).getLocation().getAltitude();
        } finally {
            lock.unlock();
        }
    }



    public boolean isSpaceFull(){
        return planes.size() >= MAX_CAPACITY;
    }

    public void removePlaneFromSpace(Plane plane) {
        planes.remove(plane);
    }
}
