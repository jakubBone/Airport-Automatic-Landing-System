package unit_tests.flight;

import com.jakub.bone.domain.airport.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.jakub.bone.domain.plane.Plane;

import static org.junit.jupiter.api.Assertions.*;

class LocationUnitTest {
    @Test
    @DisplayName("Plane should update its location correctly")
    void testUpdateLocation() {
        Plane plane = new Plane("TEST_PLANE");
        Location initLocation = new Location(1000, 1000, 1000);
        Location newLocation = new Location(5000, 5000, 5000);

        // Assign initial location
        plane.getNavigator().setLocation(initLocation);

        // Update location
        plane.getNavigator().updateLocation(newLocation, plane.getFlightNumber());

        assertNotEquals(plane.getNavigator().getLocation(), initLocation, "Plane should not remain at the initial location");
        assertEquals(plane.getNavigator().getLocation(), newLocation, "Plane should have the new location after update");
    }

    @Test
    @DisplayName("Location equals() should distinguish different coordinates")
    void testEqualLocationIdentification(){
        // Two planes with different locations
        Plane plane1 = new Plane("TEST_PLANE_1");
        plane1.getNavigator().setLocation(new Location(1000, 1000, 1000));

        Plane plane2 = new Plane("TEST_PLANE_2");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 5000));
        // Compare the locations
        boolean isLocationEqual = plane1.getNavigator()
                                        .getLocation()
                                        .equals(plane2.getNavigator().getLocation());

        assertFalse(isLocationEqual, "Locations with different coordinates should not be equal");
    }
}
