package plane;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuelManager {
    private double consumptionPerHour;
    private double consumptionPerSecond;
    private double fuelLevel;
    public FuelManager() {
        this.consumptionPerHour = 2000;
        this.consumptionPerSecond = consumptionPerHour / 3600;
        this.fuelLevel = consumptionPerHour * 3;
    }
    public void burnFuel() {
        fuelLevel -= consumptionPerSecond;
    }

    public boolean isOutOfFuel() {
        return fuelLevel <= 0;
    }

    /*public FuelManager() {
        this.fuelConsumptionPerHour = 2000.0;
        this.fuelLevel = fuelConsumptionPerHour * 3;
    }
    public void burnFuel() {
        double fuelConsumptionPerSec = fuelConsumptionPerHour / 3600;
        fuelLevel -= fuelConsumptionPerSec;
    }
    public boolean isOutOfFuel() {
        return this.fuelLevel <= 0;
    }*/

}
