package unit_tests.flight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.FuelManager;
import plane.Plane;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuelManagerUnitTest {
    @Test
    @DisplayName("Should test fuel reducing after plane move")
    void testBurnFuel() {
        Plane plane = new Plane("TEST_PLANE");
        double consumption = plane.getFuelManager().getConsumptionPerSecond();

        double initialFuelLevel = plane.getFuelManager().getFuelLevel();
        plane.getFuelManager().burnFuel();
        double currentFuelLevel = plane.getFuelManager().getFuelLevel();

        assertEquals(currentFuelLevel, initialFuelLevel - consumption, "TEST_PLANE fuel level should be reduced");
    }
}
