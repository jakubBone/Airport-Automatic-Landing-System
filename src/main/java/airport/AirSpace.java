package airport;

import plane.Location;
import plane.Plane;

import java.util.ArrayList;
import java.util.Random;

public class AirSpace {
    private static final int MAX_PLANES_IN_SPACE = 100;
    private static final int AIRSPACE_SIDE_LENGTH = 10000; // meters
    private static final int AIRSPACE_ALTITUDE = 5000; // meters
    private static ArrayList<Plane> planes = new ArrayList<>();

    public void registerPlaneInAirSpace(Plane plane){
       planes.add(plane);
       plane.setLocation(getInitialRandomLocation());
    }
    public Location getInitialRandomLocation(){
        Random random = new Random();
        double randomX = random.nextDouble();
        double randomY = random.nextDouble();
        double randomAltitude = random.nextDouble();

        return new Location(randomX, randomY, randomAltitude);
    }

    public void updatePlanes(){
        for(Plane plane: planes){
            if(plane.isOutOfFuel() || checkCollision(plane)){
                planes.remove(plane);
            }
        }
    }

    public boolean checkCollision(Plane plane){
            for(Plane other: planes){
                if(arePlanesClose(plane, other))
                    return true;
            }
    }

    private boolean arePlanesClose(Plane plane1, Plane plane2) {
        double deltaX = plane1.getLocation().getX() - plane2.getLocation().getX();
        double deltaY = plane1.getLocation().getY() - plane2.getLocation().getY();
        double deltaAltitude = plane1.getLocation().getAltitude() - plane2.getLocation().getAltitude();

        double distanceSquared = deltaX * deltaX + deltaY * deltaY + deltaAltitude * deltaAltitude;

        return distanceSquared < 100; // 10^2 = 100
    }

    public boolean isAirSpaceFull(){
        return planes.size() >= MAX_PLANES_IN_SPACE;
    }
}
