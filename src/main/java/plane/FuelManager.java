package plane;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuelManager {
    private double fuelConsumptionPerHour;
    private double fuelLevel;

    public FuelManager() {
        this.fuelConsumptionPerHour = 2000.0;
        this.fuelLevel = fuelConsumptionPerHour * 3;
    }
    public void burnFuel() {
        double fuelConsumptionPerSec = fuelConsumptionPerHour / 3600;
        fuelLevel -= fuelConsumptionPerSec;
    }
    public boolean isOutOfFuel() {
        return this.fuelLevel <= 0;
    }
}
