package unit_tests.flight;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jakub.bone.domain.plane.Plane;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FuelManagerUnitTest {
    @Test
    @DisplayName("Fuel level should decrease by the consumption rate after burnFuel() call")
    void testBurnFuel() {
        Plane plane = new Plane("TEST_PLANE");

        // Capture the plane's current consumption rate
        double consumption = plane.getFuelManager().getConsumptionPerSecond();

        // Record initial fuel level
        double initialFuelLevel = plane.getFuelManager().getFuelLevel();

        // Trigger fuel burn
        plane.getFuelManager().burnFuel();

        // Verify the fuel level decreased by exactly the consumption rate
        double currentFuelLevel = plane.getFuelManager().getFuelLevel();

        assertEquals(currentFuelLevel, initialFuelLevel - consumption, "TEST_PLANE fuel level should be reduced");
    }
}
