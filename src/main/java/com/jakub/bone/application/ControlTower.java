package com.jakub.bone.application;

import com.jakub.bone.domain.airport.Runway;
import com.jakub.bone.database.AirportDatabase;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.domain.plane.Plane;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import static com.jakub.bone.utills.Constant.*;

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
        executeWithLock(() -> {
            planes.add(plane);
            database.getPLANE_DAO().registerPlaneInDB(plane);
        });
    }

    public boolean isSpaceFull() {
        return executeWithLock(() -> planes.size() >= MAX_CAPACITY);
    }

    public boolean isAtCollisionRiskZone(Plane plane) {
        return executeWithLock(() -> planes.stream()
                .anyMatch(otherPlane -> plane.getNavigator().getRiskZoneWaypoints()
                .contains(otherPlane.getNavigator().getLocation())));
    }

    public boolean isRunwayAvailable(Runway runway){
        return executeWithLock(runway::isAvailable);
    }

    public void assignRunway(Runway runway) {
        executeWithLock(() -> runway.setAvailable(false));
    }

    public void releaseRunway(Runway runway) {
        executeWithLock(() -> runway.setAvailable(true));
    }

    public void releaseRunwayIfPlaneAtFinalApproach(Plane plane, Runway runway){
        if(plane.getNavigator().getLocation().equals(runway.getCorridor().getFinalApproachPoint())){
            releaseRunway(runway);
        }
    }

    public void removePlaneFromSpace(Plane plane) {
        executeWithLock(() -> planes.remove(plane));
    }

    public boolean isPlaneApproachingHoldingAltitude(Plane plane) {
        return plane.getNavigator().getLocation().getAltitude() == HOLDING_ENTRY_ALTITUDE;
    }

    public boolean hasLandedOnRunway(Plane plane, Runway runway){
        boolean hasLanded = plane.getNavigator().getLocation().equals(runway.getLandingPoint());
        if (hasLanded) {
            database.getPLANE_DAO().registerLandingInDB(plane);
        }
        return hasLanded;
    }

    public Plane getPlaneByFlightNumber(String flightNumber){
        return executeWithLock(() -> planes.stream()
                .filter(plane -> flightNumber.equals(plane.getFlightNumber()))
                .findFirst()
                .orElse(null));
    }


    // Helper methods for locks management
    private <T> T executeWithLock(Supplier<T> action){
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    private void executeWithLock(Runnable action){
        lock.lock();
        try {
            action.run();
        } finally {
            lock.unlock();
        }
    }
}