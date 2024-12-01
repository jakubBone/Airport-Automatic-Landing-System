package controller;

import airport.Airport;
import airport.Runway;
import location.Location;
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
        lock.lock();
        try {
            planes.add(plane);
        } finally {
            lock.unlock();
        }
    }
    public boolean isSpaceFull(){
        lock.lock();
        try {
            return planes.size() >= Airport.MAX_CAPACITY;
        } finally {
            lock.unlock();
        }
    }

    public boolean isLocationOccupied(Plane plane1) {
        lock.lock();
        Location plane1Location = plane1.getLocation();
        try {
            for (Plane plane2 : planes) {
                if (plane2.getLocation().equals(plane1Location)) {
                 return true;
             }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isCollisionRisk(Plane plane1) {
        lock.lock();
        Location waypoint = new Location(5000, -1000, 1000);
        try {
            for (Plane plane2 : planes) {
                if(plane1.getId() != plane2.getId()){
                    if (waypoint.equals(plane2.getLocation())) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunwayAvailable(Runway runway){
       lock.lock();
       try {
           return runway.isAvailable();
       } finally {
          lock.unlock();
       }
    }

    public void assignRunway(Runway runway){
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
        lock.lock();
        try {
            planes.remove(plane);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void checkCollision() {
        lock.lock();
        try{
            for (int i = 0; i < planes.size(); i++) {
                Plane plane1 = planes.get(i);
                for (int j = i + 1; j < planes.size(); j++) {
                    Plane plane2 = planes.get(j);
                    if (plane1.getLocation().equals(plane2.getLocation()) &&
                            plane1.getCurrentWaypointIndex() == plane2.getCurrentWaypointIndex()) {

                        planes.get(i).setDestroyed(true);
                        planes.get(j).setDestroyed(true);

                        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getId(), plane2.getId());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isPlaneApproachingHoldingAltitude(Plane plane) {
        int entryPointAltitude = 1013;
        return plane.getLocation().getAltitude() == entryPointAltitude;
    }
    public boolean isPlaneLeavingAlternativeHolding(Plane plane) {
        Location leavingWaypoint = new Location(-5000, 4500, 4000);
        return plane.getLocation().equals(leavingWaypoint);
    }

    public boolean isRunwayCollision(Plane plane) {
        return plane.getLocation().getAltitude() < 0;
    }

    public boolean hasLandedOnRunway(Plane plane, Runway runway){
        return (plane.getLocation().equals(runway.getLandingPoint()));
    }
}