package airport;

import lombok.Getter;
import plane.Plane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class AirSpace {
    private static final int MAX_CAPACITY = 100;
    private final int AIRSPACE_SIDE_LENGTH = 100; // meters
    private final int AIRSPACE_ALTITUDE = 500; // meters
    private static ArrayList<Plane> planesInSpace = new ArrayList<>();
    private List<WayPoint> wayPoints;
    private Plane[][][] space;

    public AirSpace() {
        space = new Plane[AIRSPACE_SIDE_LENGTH][AIRSPACE_SIDE_LENGTH][AIRSPACE_ALTITUDE];
        initWayPoints();
    }

    public void initWayPoints(){
        wayPoints= Arrays.asList(){
            new WayPoint(5000, 0),    // E
                    new WayPoint(3535, 3535), // NE
                    new WayPoint(0, 5000),    // N
                    new WayPoint(-3535, 3535),// NW
                    new WayPoint(-5000, 0),   // W
                    new WayPoint(-3535, -3535),// SW
                    new WayPoint(0, -5000),   // S
                    new WayPoint(3535, -3535) // SE
        }
    }

    public void registerPlane(Plane plane){
       planesInSpace.add(plane);
       space[plane.getLocation().getX()][plane.getLocation().getY()][plane.getLocation().getAltitude()] = plane;
    }

    public void setPlanePosition(Plane plane, int x, int y, int z){
        clearPlanePosition(plane.getLocation().getX(), plane.getLocation().getY(), plane.getLocation().getAltitude());
        space[x][y][z] = plane;
    }

    public void clearPlanePosition(int x, int y, int z){
        space[x][y][z] = null;
    }

    /*public boolean checkCollision(Plane plane){
        for(Plane other: planesInSpace){
            if(arePlanesTooClose(plane, other))
                return true;
        }
        return false;
    }*/

    /*private boolean arePlanesTooClose(Plane plane1, Plane plane2) {

    }*/

    public boolean isSpaceFull(){
        return planesInSpace.size() >= MAX_CAPACITY;
    }

    public void removePlaneFromSpace(Plane plane) {
        planesInSpace.remove(plane);
    }


}
