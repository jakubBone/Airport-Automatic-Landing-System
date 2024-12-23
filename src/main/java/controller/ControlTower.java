package controller;

import airport.Airport;
import airport.Runway;
import database.AirportDatabase;
import location.Location;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Plane;
import utills.Constant;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
@Getter
public class ControlTower {
    private List<Plane> planes;
    private Lock lock;
    private AirportDatabase database;

    public ControlTower(AirportDatabase database) throws SQLException {
        this.planes = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.database = database;
    }

    public void registerPlane(Plane plane) {
        lock.lock();
        try {
            planes.add(plane);
            database.getPLANE_DAO().registerPlaneInDB(plane);
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

    public boolean isAtCollisionRiskZone(Plane plane1) {
        lock.lock();
        try {
            for (Plane plane2 : planes) {
                for(Location waypoint: plane1.getNavigator().getRiskZoneWaypoints()){
                    if(waypoint.equals(plane2.getNavigator().getLocation())) {
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

    public void releaseRunwayIfPlaneAtFinalApproach(Plane plane, Runway runway){
        if(plane.getNavigator().getLocation().equals(runway.getCorridor().getFinalApproachPoint())){
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

                    Location loc1 = plane1.getNavigator().getLocation();
                    Location loc2 = plane2.getNavigator().getLocation();
                    if (arePlanesToClose(loc1, loc2)) {
                            collidedID[0] = plane1.getFlightNumber();
                            collidedID[1] = plane2.getFlightNumber();
                            database.getCOLLISION_DAO().registerCollisionToDB(collidedID);
                            planes.get(i).setDestroyed(true);
                            planes.get(j).setDestroyed(true);
                        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getFlightNumber(), plane2.getFlightNumber());
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean arePlanesToClose(Location loc1, Location loc2) {
        double distance = Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2)
                + Math.pow(loc1.getY() - loc2.getY(), 2)
                + Math.pow(loc1.getAltitude() - loc2.getAltitude(), 2));
        return distance <= 10;
    }

    public boolean isPlaneApproachingHoldingAltitude(Plane plane) {
        return plane.getNavigator().getLocation().getAltitude() == Constant.HOLDING_ENTRY_ALTITUDE;
    }

    public boolean hasLandedOnRunway(Plane plane, Runway runway){
        boolean hasLanded = false;
        if(plane.getNavigator().getLocation().equals(runway.getLandingPoint())){
            hasLanded = true;
            database.getPLANE_DAO().registerLandingInDB(plane);
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