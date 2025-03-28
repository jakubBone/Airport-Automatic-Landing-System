package com.jakub.bone.domain.plane;

import com.jakub.bone.domain.airport.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.jakub.bone.utils.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import static com.jakub.bone.config.Constant.*;

@Getter
@Setter
@Log4j2
public class Navigator {
    private List<Location> waypoints;
    private FuelManager fuelManager;
    private int currentIndex;
    private Location location;
    private boolean isFirstMove;

    public Navigator(FuelManager fuelManager) {
        this.waypoints = WaypointGenerator.getDescentWaypoints();
        this.fuelManager = fuelManager;
        this.isFirstMove = true;
        spawnPlane();
    }

    public void move() {
        if (currentIndex < waypoints.size()) {
            updateLocation(waypoints.get(currentIndex));
            currentIndex++;
        }
        fuelManager.burnFuel();
    }

    public boolean isAtLastWaypoint(){
        return currentIndex == waypoints.size();
    }

    public void updateLocation(Location location) {
        if(!isFirstMove){
            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException ex) {
                log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }
        this.isFirstMove = false;
        this.location = location;
    }

    private void spawnPlane() {
        List<Location> waypointsToSpawn = waypoints.stream()
                .filter(wp -> wp.getAltitude() >= MIN_ALTITUDE && wp.getAltitude() <= MAX_ALTITUDE)
                .collect(Collectors.toList());

        Random random = new Random();
        this.currentIndex = random.nextInt(waypointsToSpawn.size());
        Location initialWaypoint = waypointsToSpawn.get(currentIndex);
        this.location = initialWaypoint;
    }

    public List <Location> getRiskZoneWaypoints() {
        List<Location> nearWaypoints = new ArrayList<>();
        for (int offset = -3; offset <= 3; offset++) {
            int index = currentIndex + offset;
            if (index > 0 && index <= waypoints.size()) {
                nearWaypoints.add(waypoints.get(index));
            }
        }
        return nearWaypoints;
    }
}

