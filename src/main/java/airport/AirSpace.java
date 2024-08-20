package airport;

import lombok.Getter;
import plane.Plane;

import java.util.ArrayList;

@Getter
public class AirSpace {
    private static final int MAX_CAPACITY = 500;
    private final int AIRSPACE_SIDE_LENGTH = 500; // meters
    private final int AIRSPACE_ALTITUDE = 500; // meters
    private static ArrayList<Plane> planesInSpace = new ArrayList<>();
    private Plane[][][] space;

    public AirSpace() {
        space = new Plane[AIRSPACE_SIDE_LENGTH][AIRSPACE_SIDE_LENGTH][AIRSPACE_ALTITUDE];
    }

    public void registerPlane(Plane plane){
        int x = plane.getLocation().getX();
        int y = plane.getLocation().getY();
        int altitude = plane.getLocation().getAltitude();

        // Only add 500 if the coordinate is negative
        if (x < 0) {
            x += 500;
        }

        if (y < 0) {
            y += 500;
        }

        planesInSpace.add(plane);
        space[x][y][altitude] = plane;
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
