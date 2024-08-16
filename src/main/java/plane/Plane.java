package plane;

import airport.AirSpace;
import airport.WayPoint;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Getter
@Setter
public class Plane implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int currentWayPoint = 0;
    private int id;
    private double fuelLevel;
    private boolean hasLanded;
    private Location location;
    private AirSpace airSpace;

    public Plane() {
        this.id = generateID();
        this.fuelLevel = 10;
        this.location = new Location(10, 10, 10);
        this.airSpace = new AirSpace();
        //this.location = generateRandomLocation();
    }

    public void holdPattern(){
        if (currentWayPoint >= airSpace.getWayPoints().size()) {
            currentWayPoint = 0; // return to 1st waypoint
        }

        WayPoint nextWayPoint = airSpace.getWayPoints().get(currentWayPoint);
        this.location.setX(nextWayPoint.getX());
        this.location.setY(nextWayPoint.getY());
        this.location.setAltitude(this.location.getAltitude() - 100);
        currentWayPoint++;

        fuelLevel -= 10;
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location generateRandomLocation(){
        Random random = new Random();
        int randomX = random.nextInt();
        int randomY = random.nextInt();
        int randomAltitude = 2000;

        return new Location(randomX, randomY, randomAltitude);
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }
}
