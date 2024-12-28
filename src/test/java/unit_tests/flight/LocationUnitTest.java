package unit_tests.flight;

import location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.Plane;

import static org.junit.jupiter.api.Assertions.*;

class LocationUnitTest {
    @Test
    @DisplayName("Should test plane location updating")
    void testUpdateLocation() {
        Plane plane = new Plane("TEST_PLANE");
        Location initLocation = new Location(1000, 1000, 1000);
        Location newLocation = new Location(5000, 5000, 5000);
        plane.getNavigator().setLocation(initLocation);

        plane.getNavigator().updateLocation(newLocation, plane.getFlightNumber());

        assertNotEquals(plane.getNavigator().getLocation(), initLocation, "TEST_PLANE should not have init location");
        assertEquals(plane.getNavigator().getLocation(), newLocation, "TEST_PLANE should update location");
    }

    @Test
    @DisplayName("Should test appropriate location equals method override")
    void testEqualLocationIdentification(){
        Plane plane1 = new Plane("TEST_PLANE_1");
        plane1.getNavigator().setLocation(new Location(1000, 1000, 1000));

        Plane plane2 = new Plane("TEST_PLANE_2");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 5000));

        boolean isLocationEqual = plane1.getNavigator().getLocation().equals(plane2.getNavigator().getLocation());

        assertFalse(isLocationEqual, "Equals method should return false");
    }
}
