package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.util.ArrayList;
import java.util.LinkedList;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Getter
public class AirTrafficController {
    public Queue <Runway> availableRunways = new LinkedList<>();
    private List<Plane> planes;
    private AirSpace space;
    private Lock lock;

    public AirTrafficController() {
        this.planes = new ArrayList<>
        this.space = new AirSpace();
        this.lock = new ReentrantLock();
        initAvailableRunways();
    }
    public void registerPlane(Plane plane) {
        planes.add(plane);
    }
    public boolean isSpaceFull(){
        return planes.size() >= AirSpace.MAX_CAPACITY;
    }

    public void initAvailableRunways(){
        availableRunways.add(space.getRunway1());
        availableRunways.add(space.getRunway2());
    }

    public Runway getAvailableRunway() {
        lock.lock();
        try {
            return availableRunways.poll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isAnyRunwayAvailable(){
       lock.lock();
       try {
           return !availableRunways.isEmpty();
       } finally {
          lock.unlock();
       }
    }

    public void releaseRunway(Runway runway) {
        lock.lock();
        try {
            availableRunways.add(runway);
        } finally {
            lock.unlock();
        }
    }

    public void removePlaneFromSpace(Plane plane) {
        planes.remove(plane);
    }
}