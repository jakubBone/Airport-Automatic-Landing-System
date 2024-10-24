package unit;

import airport.AirTrafficController;
import airport.Corridor;
import airport.Runway;
import location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.Plane;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirTrafficControllerTest {
    AirTrafficController controller;
    List<Plane> incomingPlanes;
    @BeforeEach
    void setUp(){
        this.controller = new AirTrafficController();
        this.incomingPlanes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return true when all planes registered")
    void testRegisterPlane(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane());
        }

        for(Plane incoming: incomingPlanes){
            controller.registerPlane(incoming);
        }

        assertTrue(controller.getPlanes().size() == 10);
    }

    @Test
    @DisplayName("Should return false when space is not full")
        void testIsSpaceFull(){
        for (int i = 0; i < 101; i++){
            incomingPlanes.add(new Plane());
        }

        for(Plane incoming: incomingPlanes){
            controller.registerPlane(incoming);
        }

        assertTrue(controller.isSpaceFull());
    }

    @Test
    @DisplayName("Should return true when location is occupied")
    void testLocationOccupied(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane());
        }

        for(Plane incoming: incomingPlanes){
            controller.registerPlane(incoming);
        }


        for (int i = 0; i < 10; i++){
            incomingPlanes.get(i).setLocation(new Location(5000, 5000, 4000));
        }

        Plane plane1 = new Plane();
        plane1.setLocation(new Location(5000, 5000, 4000));
        Plane plane2 = new Plane();
        plane2.setLocation(new Location(4000, 4000, 3000));

        assertTrue(controller.isLocationOccupied(plane1));
    }

    @Test
    @DisplayName("Should return true when runway is available")
    void testIsRunwayAvailable(){
        // Runway set as available
        Runway runway = new Runway("R-1", new Location(1000, -2000, 0), new Corridor( "C-1", new Location(1000, -2000, 0)), true);

        assertTrue(controller.isRunwayAvailable(runway));
    }

    @Test
    @DisplayName("Should return false when runway is unavailable")
    void testIsRunwayUnavailable(){
        // Runway set as unavailable
        Runway runway = new Runway("R-2", new Location(1000, 2000, 0), new Corridor( "C-1", new Location(1000, 2000, 0)), false);

        assertFalse(controller.isRunwayAvailable(runway));
    }

    @Test
    @DisplayName("Should return false when runway is occupied")
    void testAssignRunway(){
        Runway runway = new Runway("R-1", new Location(1000, 2000, 0), new Corridor( "C-1", new Location(1000, 2000, 0)), true);

        controller.assignRunway(runway);

        assertFalse(runway.isAvailable());
    }

    @Test
    @DisplayName("Should return true when runway is occupied")
    void testReleaseRunway(){
        Runway runway = new Runway("R-1", new Location(1000, 2000, 0), new Corridor( "C-1", new Location(1000, 2000, 0)), false);
        controller.releaseRunway(runway);

        assertTrue(runway.isAvailable());
    }

    @Test
    @DisplayName("Should return true when space doesn't contain plane in the space")
    void testRemovePlaneFromSpace(){
        Plane plane = new Plane();

        controller.registerPlane(plane);

        controller.removePlaneFromSpace(plane);

        assertTrue(!controller.getPlanes().contains(plane));
    }

    @Test
    @DisplayName("Should return true when collided planes removed from the airspace")
    void testIfCollidedPlanesRemoved(){
        Plane plane1 = new Plane();
        plane1.setLocation(new Location(5000, 5000, 4000));
        plane1.setCurrentWaypointIndex(20);
        Plane plane2 = new Plane();
        plane2.setLocation(new Location(5000, 5000, 4000));
        plane2.setCurrentWaypointIndex(20);

        controller.registerPlane(plane1);
        controller.registerPlane(plane2);

        controller.checkCollision();

        assertTrue(controller.getPlanes().isEmpty());
    }

    @Test
    @DisplayName("Should return true when planes altitude is under 0")
    void testIsRunwayCollsion(){
        Plane plane1 = new Plane();
        plane1.setLocation(new Location(5000, 5000, - 10));

        assertTrue(controller.isRunwayCollision(plane1));
    }

    @Test
    @DisplayName("Should return true when planes has landed")
    void testHasLandedOnRunway(){
        Plane plane1 = new Plane();
        plane1.setLocation(new Location(1000, -2000, 0));
        Runway runway = new Runway("R-1", new Location(1000, -2000, 0), new Corridor( "C-1", new Location(1000, -2000, 0)), true);

        assertTrue(controller.hasLandedOnRunway(plane1, runway));
    }

}
