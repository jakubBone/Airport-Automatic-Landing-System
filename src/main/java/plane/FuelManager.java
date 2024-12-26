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
        this.fuelLevel = consumptionPerSecond * 3;
    }
    public void burnFuel() {
        fuelLevel -= consumptionPerSecond;
    }

    public boolean isOutOfFuel() {
        return this.fuelLevel <= 0;
    }

}
