package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Plane;
import java.util.ArrayList;

import java.util.List;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Getter
public class AirTrafficController {
    private List<Plane> planes;
    private Lock lock;

    public AirTrafficController() {
        this.planes = new ArrayList<>();
        this.lock = new ReentrantLock();
    }
    public void registerPlane(Plane plane) {
        planes.add(plane);
    }
    public boolean isSpaceFull(){
        return planes.size() >= Airport.MAX_CAPACITY;
    }

    public void lockRunway(Runway runway){
       lock.lock();
       try {
            runway.setAvailable(false);
       } finally {
          lock.unlock();
       }
    }
    public void releaseRunway(Runway runway){
       lock.lock();
       try {
            runway.setAvailable(true);
       } finally {
          lock.unlock();
       }
    }

    public void removePlaneFromSpace(Plane plane) {
        planes.remove(plane);
    }
}