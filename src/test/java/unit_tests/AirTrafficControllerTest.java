package unit_tests;

import controller.AirTrafficController;
import airport.Corridor;
import airport.Runway;
import database.AirportDatabase;
import location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.Plane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AirTrafficControllerTest {
    AirTrafficController controller;
    List<Plane> incomingPlanes;
    @BeforeEach
    void setUp() throws SQLException {
        AirportDatabase database = new AirportDatabase();
        this.controller = new AirTrafficController(database);
        this.incomingPlanes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return true when all planes registered")
    void testRegisterPlane(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane("x"));
        }

        for(Plane incoming: incomingPlanes){
            controller.registerPlane(incoming);
        }

        assertTrue(controller.getPlanes().size() == 10);
    }

    @Test
    @DisplayName("Should return false when space is not full")
        void testIsSpaceFull(){
        for (int i = 0; i < 110; i++){
            incomingPlanes.add(new Plane("x"));
        }

        for(Plane incoming: incomingPlanes){
            controller.registerPlane(incoming);
        }

        assertTrue(controller.isSpaceFull());
    }

    @Test
    @DisplayName("Should return true when collided planes destroyed ")
    void tesPlanesCollision(){
        Plane plane1 = new Plane("x");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4010));
        Plane plane2 = new Plane("y");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controller.registerPlane(plane1);
        controller.registerPlane(plane2);

        controller.checkCollision();

        assertTrue(plane1.isDestroyed());
        assertTrue(plane2.isDestroyed());
    }

    @Test
    @DisplayName("Should return true when runway is available")
    void testIsRunwayAvailable(){
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor("C-1", new Location(-5000, 3000, 1000), new Location(-3000, 1000, 700)));
        assertTrue(controller.isRunwayAvailable(runway));
    }

    @Test
    @DisplayName("Should return false when runway is unavailable")
    void testIsRunwayUnavailable(){
        // Runway set as unavailable
        Runway runway = new Runway("R-2", new Location(500, -2000, 0), new Corridor( "C-1", new Location(-5000, 0, 1000), new Location(-3000, -2000, 700)));
        runway.setAvailable(false);
        assertFalse(controller.isRunwayAvailable(runway));
    }

    @Test
    @DisplayName("Should return false when runway is occupied")
    void testAssignRunway(){
        Runway runway = new Runway("R-1", new Location(1500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        controller.assignRunway(runway);

        assertFalse(runway.isAvailable());
    }

    @Test
    @DisplayName("Should return true if runway released")
    void testReleaseRunway(){
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));
        controller.releaseRunway(runway);

        assertTrue(runway.isAvailable());
    }

    @Test
    @DisplayName("Should return true when runway is released after across final approach point")
    void testReleaseRunwayIdPlaneFinalAtApproach(){
        Plane plane = new Plane("X");
        plane.getNavigator().setLocation(new Location(-3000, 1000, 700));
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        controller.releaseRunwayIfPlaneAtFinalApproach(plane, runway);

        assertTrue(runway.isAvailable());
    }

    @Test
    @DisplayName("Should return true when no one plane in the space")
    void testRemovePlaneFromSpace(){
        Plane plane = new Plane("x");

        controller.registerPlane(plane);

        controller.removePlaneFromSpace(plane);

        assertTrue(!controller.getPlanes().contains(plane));
    }

    @Test
    @DisplayName("Should return true when planes has landed")
    void testHasLandedOnRunway(){
        Plane plane1 = new Plane("x");
        plane1.getNavigator().setLocation(new Location(500, 1000, 0));
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        assertTrue(controller.hasLandedOnRunway(plane1, runway));
    }

}
