package unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import plane.FuelManager;
import plane.Plane;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuelManagerUnitTest {
    FuelManager fuelManager;

    @BeforeEach
    void setUp() {
        this.fuelManager = new FuelManager();
    }

    @Test
    void testBurnFuel() {
        Plane plane = new Plane("0000");
        double x = plane.getFuelManager().getFuelLevel();
        plane.getFuelManager().burnFuel();
        double y = plane.getFuelManager().getFuelLevel();

        assertEquals(y, x - plane.getFuelManager().getConsumptionPerSecond());

    }
}
