package airport;

import location.Location;
import location.WaypointGenerator;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Getter
public class AirTrafficController {
    public Queue <Runway> availableRunways = new LinkedList<>();
    private Lock lock;

    public AirTrafficController() {
        this.lock = new ReentrantLock();;
        initRunways();
    }

    public void initRunways(){
        createRunwayWithCorridor("R-1", "C-1", new Location(1000, 2000, 0), new Location(-5000, 2000, 2000));
        createRunwayWithCorridor("R-2", "C-2", new Location(1000, -2000, 0), new Location(-5000, -2000, 2000));
    }
    public void createRunwayWithCorridor(String runwayId, String corridorId, Location touchdownPoint, Location corridorEntryPoint){
        Corridor corridor = new Corridor(corridorId, corridorEntryPoint);
        Runway runway = new Runway(runwayId, touchdownPoint, corridor);
        availableRunways.add(runway);
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
            lock.lock();
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
}