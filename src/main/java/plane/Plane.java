package plane;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Plane implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int planeId;
    private double fuelLevel;
    private double heading;
    private double speed;
    private Location currentLocation;

    public Plane() {
        this.planeId = generateID();
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public void spawnPlaneAtRandomLocation(){
        Random random = new Random();
        double randomX = random.nextDouble();
        double randomY = random.nextDouble();
        double randomAltitude = random.nextDouble();

        Location randomLocation = new Location(randomX, randomY, randomAltitude);
        setCurrentLocation(randomLocation);
    }

    public void reduceFuel(){

    }

    public void updateLocation(){

    }
}
