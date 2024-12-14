package controller;

import airport.Airport;
import airport.Runway;
import database.AirportDatabase;
import location.Location;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Getter
public class AirTrafficController {
    private List<Plane> planes;
    private Lock lock;
    private AirportDatabase database;

    public AirTrafficController(AirportDatabase database) throws SQLException {
        this.planes = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.database = database;
    }

    public void registerPlane(Plane plane) {
        lock.lock();
        try {
            planes.add(plane);
            database.getPlaneDAO().registerPlaneInDB(plane);
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
        Location plane1Location = plane1.getNavigator().getLocation();
        try {
            for (Plane plane2 : planes) {
                if (plane2.getNavigator().getLocation().equals(plane1Location)) {
                 return true;
             }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isHoldingCollisionRisk(Plane plane1) {
        lock.lock();
        Location waypoint = new Location(5000, -1000, 1000);
        try {
            for (Plane plane2 : planes) {
                if(!plane1.getFlightNumber().equals(plane2.getFlightNumber())){
                    if (waypoint.equals(plane2.getNavigator().getLocation())) {
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

    public void releaseRunwayIfPlaneAtSecondEntryPoint(Plane plane, Runway runway){
        if(plane.getNavigator().getLocation().equals(runway.getCorridor().getSecondEntryPoint())){
            releaseRunway(runway);
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
    public void checkCollision() {
        lock.lock();
        String [] collidedID = new String[2];
        try{
            for (int i = 0; i < planes.size(); i++) {
                Plane plane1 = planes.get(i);
                for (int j = i + 1; j < planes.size(); j++) {
                    Plane plane2 = planes.get(j);
                    if (arePlanesToClose(plane1, plane2)) {
                        //if(!(collidedID[0].equals(plane1.getFlightNumber())) && !(collidedID[1].equals(plane2.getFlightNumber()))){
                            collidedID[0] = plane1.getFlightNumber();
                            collidedID[1] = plane2.getFlightNumber();
                            database.getCollisionDAO().registerCollisionToDB(collidedID);
                            planes.get(i).setDestroyed(true);
                            planes.get(j).setDestroyed(true);
                       // }

                        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getFlightNumber(), plane2.getFlightNumber());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean arePlanesToClose(Plane plane1, Plane plane2) {
        Location loc1 = plane1.getNavigator().getLocation();
        Location loc2 = plane2.getNavigator().getLocation();
        double distance = Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2)
                + Math.pow(loc1.getY() - loc2.getY(), 2)
                + Math.pow(loc1.getAltitude() - loc2.getAltitude(), 2));
        return distance < 10;
    }

    public boolean isPlaneApproachingHoldingAltitude(Plane plane) {
        int entryPointAltitude = 1013;
        return plane.getNavigator().getLocation().getAltitude() == entryPointAltitude;
    }
    public boolean isPlaneApproachHoldingEntry(Plane plane) {
        Location leavingWaypoint = new Location(-5000, 4500, 4000);
        return plane.getNavigator().getLocation().equals(leavingWaypoint);
    }

    public boolean hasLandedOnRunway(Plane plane, Runway runway){
        boolean hasLanded = false;
        if(plane.getNavigator().getLocation().equals(runway.getLandingPoint())){
            hasLanded = true;
            database.getPlaneDAO().registerLandingInDB(plane);
        }
        return hasLanded;
    }

    public Plane getPlaneByFlightNumber(String flightNumber){
        for(Plane plane: planes){
            if(flightNumber.equals(plane.getFlightNumber())){
                return plane;
            };
        }
        return null;
    }
}