package plane;

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
    private int id;
    private double fuelLevel;
    private boolean hasLanded;
    private Location location;

    public Plane() {
        this.id = generateID();
        this.fuelLevel = 10;
        this.location = new Location(10, 10, 10);
        //this.location = generateRandomLocation();
    }

    public void holdPattern(){
        move(1, 1, 1); // example movement
    }

    public void move(int deltaX, int deltaY, int deltaAltitude){
        this.location.setX(location.getX() + deltaX);
        this.location.setY(location.getY() + deltaY);
        this.location.setAltitude(location.getAltitude() + deltaAltitude);
    }

    /*public void directLanding(Runway runway){
        double deltaX = runway.getLocation().getX() - location.getX();
        double deltaY = runway.getLocation().getY() - location.getY();

        double distanceToRunway = deltaX * deltaX + deltaY * deltaY;

        if(distanceToRunway <= 10) {
            log.info("Plane [" + getId() + "] prepared for landing");
            log.info("Plane [" + getId() + "] is landing...");
            hasLanded = true;
            log.info("Plane [" + getId() + "] has landed");
        }
    }*/

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

    public void reduceFuel(){
        fuelLevel--;
    }
}
