package airport;

import plane.Plane;

import java.util.ArrayList;

public class AirSpace {
    private static final int MAX_CAPACITY = 100;
    private final int AIRSPACE_SIDE_LENGTH = 100; // meters
    private final int AIRSPACE_ALTITUDE = 500; // meters
    private static ArrayList<Plane> planesInSpace = new ArrayList<>();
    private Plane[][][] spaceGrid;

    public AirSpace() {
        spaceGrid = new Plane[AIRSPACE_SIDE_LENGTH][AIRSPACE_SIDE_LENGTH][AIRSPACE_ALTITUDE];
    }

    public void registerPlane(Plane plane){
       planesInSpace.add(plane);
       spaceGrid[plane.getLocation().getX()][plane.getLocation().getY()][plane.getLocation().getAltitude()] = plane;
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

    public void removePlane(Plane plane) {
        planesInSpace.remove(plane);
        spaceGrid[plane.getLocation().getX()][plane.getLocation().getY()][plane.getLocation().getAltitude()] = null;
    }
}
