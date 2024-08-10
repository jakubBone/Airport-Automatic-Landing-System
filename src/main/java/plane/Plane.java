package plane;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Plane implements Serializable {
    private static final AtomicInteger idCounter = new AtomicInteger();
    private int planeId;
    private double fuelLevel;
    private double heading;
    private double speed;
    private double direction;
    private Location location;

    public Plane() {
        this.planeId = generateID();
    }

    public static int generateID() {
        return idCounter.incrementAndGet();
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }

    public void reduceFuel(){

    }

    public void updateLocation(){

    }


}
