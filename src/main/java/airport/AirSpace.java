package airport;

import plane.Plane;

import java.util.ArrayList;

public class AirSpace {
    private static final int MAX_CAPACITY = 100;
    private static final int AIRSPACE_SIDE_LENGTH = 10000; // meters
    private static final int AIRSPACE_ALTITUDE = 5000; // meters
    private static ArrayList<Plane> planes = new ArrayList<>();

    public void registerPlaneInAirSpace(Plane plane){
       planes.add(plane);
    }

    public void updateAirspace(){
        for(Plane plane: planes){
            if(plane.isOutOfFuel() || checkCollision(plane)){
                planes.remove(plane);
            }
        }
    }

    public boolean checkCollision(Plane plane){
        for(Plane other: planes){
            if(arePlanesTooClose(plane, other))
                return true;
        }
        return false;
    }

    private boolean arePlanesTooClose(Plane plane1, Plane plane2) {
        double deltaX = plane1.getLocation().getX() - plane2.getLocation().getX();
        double deltaY = plane1.getLocation().getY() - plane2.getLocation().getY();
        double deltaAltitude = plane1.getLocation().getAltitude() - plane2.getLocation().getAltitude();

        double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaAltitude * deltaAltitude;

        return distanceSquared < 100; // 10^2 = 100
    }

    public boolean isAirSpaceFull(){
        return planes.size() >= MAX_CAPACITY;
    }

    public void removePlaneFromAirSpace(Plane plane) {
        planes.remove(plane);
    }
}
