package plane;

import airport.Runway;
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
    private double speed;
    private double heading;
    private Location location;
    private boolean hasLanded;

    public Plane() {
        this.id = generateID();
        this.fuelLevel = 10;
        this.speed = 100;
        this.location = generatePlaneRandomLocation();
    }

    public void holdPattern(){

        location.setX(location.getX() + 1);
        location.setY(location.getY() + 1);

        // Przykład prostego orbitowania na poziomie 2000m i 1000m od środka lotniska
        //this.location.setX(this.location.getX() + Math.cos(this.heading) * 1000);
        //this.location.setY(this.location.getY() + Math.sin(this.heading) * 1000);
        //this.location.setAltitude(2000);

        /*double radius = 10000;

        double angleChange = speed / radius;
        double currentAngle = Math.atan2(location.getY(), location.getX());

        // Angle actualization
        currentAngle += angleChange;

        double newX = radius * Math.cos(currentAngle);
        double newY = radius * Math.sin(currentAngle);

        // Setting a new position
        location.setX(newX);
        location.setY(newY);*/

    }

    public void directLanding(Runway runway){
        double deltaX = runway.getLocation().getX() - location.getX();
        double deltaY = runway.getLocation().getY() - location.getY();

        double distanceToRunway = deltaX * deltaX + deltaY * deltaY;

        if(distanceToRunway <= 10) {
            log.info("Plane [" + getId() + "] prepared for landing");
            log.info("Plane [" + getId() + "] is landing...");
            hasLanded = true;
            log.info("Plane [" + getId() + "] has landed");
        }
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public Location generatePlaneRandomLocation(){
        Random random = new Random();
        double randomX = random.nextDouble();
        double randomY = random.nextDouble();
        double randomAltitude = 2000;

        return  new Location(randomX, randomY, randomAltitude);
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }

    public void reduceFuel(){
        fuelLevel--;
    }
}
