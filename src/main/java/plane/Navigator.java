package plane;

import location.Location;

import java.util.List;

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
    private boolean isFirstMove;
    private int currentIndex;
    private Location location;

    public Navigator(FuelManager fuelManager) {
        this.waypoints = WaypointGenerator.getDescentWaypoints();
        this.fuelManager = fuelManager;
        this.isFirstMove = true;
        this.currentIndex = 310;
        this.location = waypoints.get(currentIndex);
        //spawnPlane();
    }

    public void moveTowardsNextWaypoint(int id) {
        if (currentIndex < waypoints.size()) {
            updateLocation(waypoints.get(currentIndex), id);
            currentIndex++;
        }
        fuelManager.burnFuel();
    }

    public boolean isAtLastWaypoint(){
        return currentIndex == waypoints.size();
    }

    public void updateLocation(Location location, int id) {
        if(!isFirstMove){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        log.info("Plane [{}] is moving to waypoint {}: [{}, {}, {}]", id, currentIndex, location.getX(), location.getY(), location.getAltitude());
        this.location = location;
    }

    /*public void spawnPlane() {
        List<Location> waypointsToSpawn = waypoints.stream()
                .filter(wp -> wp.getAltitude() >= 2000 && wp.getAltitude() <= 5000)
                .collect(Collectors.toList());

        Random random = new Random();
        this.currentIndex = random.nextInt(waypointsToSpawn.size());
        //this.currentIndex = random.nextInt(waypointsToSpawn.size());
        Location initialWaypoint = waypointsToSpawn.get(currentIndex);
        this.location = initialWaypoint;

    }*/
}
