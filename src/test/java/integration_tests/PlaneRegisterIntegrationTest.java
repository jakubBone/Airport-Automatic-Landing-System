package integration_tests;


import location.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.Plane;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlaneRegisterIntegrationTest {


    /*@Test
    @DisplayName("Should return true when location is occupied")
    void testLocationOccupied(){
        Plane plane1 = new Plane("x");
        plane1.getNavigator().getRiskZoneWaypoints().add(new Location(5000, 5000, 4000));
        controller.registerPlane(plane1);


        Plane plane2 = new Plane("x");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        assertTrue(controller.isLocationOccupied(plane2));
    }*/

    /*@Test
    @DisplayName("Should return true when location is occupied")
    void testLocationOccupied() {
        // Arrange
        Plane plane1 = new Plane("PLANE1");
        plane1.getNavigator().setCurrentIndex(10);
        //plane1.getNavigator().setLocation(new Location(5000, 5000, 4000));
        List<Location> zone = plane1.getNavigator().getRiskZoneWaypoints();// Lokalizacja w przestrzeni powietrznej
        controller.registerPlane(plane1);

        Plane plane2 = new Plane("PLANE2");
        plane2.getNavigator().setCurrentIndex(10);
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000)); // Ten sam punkt co plane1

        // Act
        boolean isOccupied = controller.isLocationOccupied(plane2);

        // Assert
        assertTrue(isOccupied, "The location should be detected as occupied by another plane.");
    }*/
}
