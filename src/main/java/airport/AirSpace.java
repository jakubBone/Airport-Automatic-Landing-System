package airport;

import plane.Plane;

import java.util.ArrayList;

public class AirSpace {
    private static final int MAX_PLANES_IN_SPACE = 100;
    private static ArrayList<Plane> planes = new ArrayList<>();

    public boolean isAirSpaceFull(){
        return planes.size() >= MAX_PLANES_IN_SPACE;
    }
    public void registerPlaneInAirSpace(Plane plane){
       planes.add(plane);
    }
    public void removePlaneFromAirSpace(Plane plane){
        planes.remove(plane);
    }
}
