package plane;

import location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import location.WaypointGenerator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

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
        //this.currentIndex = 316;
        //this.location = waypoints.get(currentIndex);
        this.isFirstMove = true;
        spawnPlane();
    }

    public void move(String id) {
        if (currentIndex < waypoints.size()) {
            updateLocation(waypoints.get(currentIndex), id);
            currentIndex++;
        }
        fuelManager.burnFuel();
    }

    public boolean isAtLastWaypoint(){
        return currentIndex == waypoints.size();
    }

    public void updateLocation(Location location, String id) {
        if(!isFirstMove){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        this.isFirstMove = false;
        log.info("Plane [{}] is moving to waypoint {}: [{}, {}, {}]", id, currentIndex, location.getX(), location.getY(), location.getAltitude());
        this.location = location;
    }

    public void spawnPlane() {
        List<Location> waypointsToSpawn = waypoints.stream()
                .filter(wp -> wp.getAltitude() >= 2000 && wp.getAltitude() <= 5000)
                .collect(Collectors.toList());

        Random random = new Random();
        this.currentIndex = random.nextInt(waypointsToSpawn.size());
        Location initialWaypoint = waypointsToSpawn.get(currentIndex);
        this.location = initialWaypoint;
    }

    public void setLocation(Location newLocation) {
        this.location = newLocation;
    }

    public List <Location> getRiskZoneWaypoints(){
        List <Location> nearWaypoints = new ArrayList<>();
        nearWaypoints.add(waypoints.get(currentIndex - 1));
        nearWaypoints.add(waypoints.get(currentIndex - 2));
        nearWaypoints.add(waypoints.get(currentIndex - 3));
        nearWaypoints.add(waypoints.get(currentIndex));
        nearWaypoints.add(waypoints.get(currentIndex + 1));
        nearWaypoints.add(waypoints.get(currentIndex + 2));
        nearWaypoints.add(waypoints.get(currentIndex + 3));
        return nearWaypoints;
    }
}
