package unit_tests;

import location.Location;
import location.WaypointGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.Plane;

import static org.junit.jupiter.api.Assertions.*;

public class LocationUnitTest {
    @Test
    @DisplayName("Should test plane location updating")
    void testUpdateLocation() {
        Plane plane = new Plane("1111");
        Location initLocation = new Location(1000, 1000, 1000);
        Location newLocation = new Location(5000, 5000, 5000);
        plane.getNavigator().setLocation(initLocation);

        plane.getNavigator().updateLocation(newLocation, plane.getFlightNumber());

        assertNotEquals(plane.getNavigator().getLocation(), initLocation, "Plane should not have init location");
        assertEquals(plane.getNavigator().getLocation(), newLocation, "Plane should have updated location");
    }

    @Test
    @DisplayName("Should test location equals method correctness")
    void testEqualLocationIdentification(){
        Plane plane1 = new Plane("0000");
        plane1.getNavigator().setLocation(new Location(1000, 1000, 1000));

        Plane plane2 = new Plane("1111");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 5000));

        boolean isLocationEqual = plane1.getNavigator().getLocation().equals(plane2.getNavigator().getLocation());

        assertFalse(isLocationEqual, "Equals method should return false");
    }
}
