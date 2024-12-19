package unit_tests;

import controller.ControlTower;
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
    AirportDatabase database;
    ControlTower controlTower;
    List<Plane> incomingPlanes;
    @BeforeEach
    void setUp() throws SQLException {
        this.database = new AirportDatabase();
        this.controlTower = new ControlTower(database);
        this.incomingPlanes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return true when all planes registered")
    void testRegisterPlane(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane("123"));
        }

        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        assertTrue(controlTower.getPlanes().size() == 10, "Should not register plane");
    }

    @Test
    @DisplayName("Should return false when space is not full")
        void testIsSpaceFull(){
        for (int i = 0; i < 110; i++){
            incomingPlanes.add(new Plane("123"));
        }

        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        assertTrue(controlTower.isSpaceFull(), "Should inform about full airspace");
    }
    @Test
    void testIfPlaneAtCollisionRiskZone() {
        Plane plane1 = new Plane("123");
        controlTower.registerPlane(plane1);
        int index = plane1.getNavigator().getCurrentIndex();

        Plane plane2 = new Plane("321");
        plane2.getNavigator().setCurrentIndex(index + 2);

        assertTrue(controlTower.isAtCollisionRiskZone(plane2),
                "Plane 2 should be detected in Plane 1's collision risk zone");
    }

    @Test
    @DisplayName("Should return true when collided planes destroyed ")
    void tesPlanesCollision(){
        Plane plane1 = new Plane("x");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4010));
        Plane plane2 = new Plane("y");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertTrue(plane1.isDestroyed(), "Plane 1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "Plane 1 should be destroyed after collision");
    }

    @Test
    @DisplayName("Should return true when runway is available")
    void testIsRunwayAvailable(){
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor("C-1", new Location(-5000, 3000, 1000), new Location(-3000, 1000, 700)));
        assertTrue(controlTower.isRunwayAvailable(runway), "Runway should be set as available");
    }

    @Test
    @DisplayName("Should return false when runway is unavailable")
    void testIsRunwayUnavailable(){
        // Runway set as unavailable
        Runway runway = new Runway("R-2", new Location(500, -2000, 0), new Corridor( "C-1", new Location(-5000, 0, 1000), new Location(-3000, -2000, 700)));
        runway.setAvailable(false);
        assertFalse(controlTower.isRunwayAvailable(runway),"Runway should be set as unavailable" );
    }

    @Test
    @DisplayName("Should return false when runway is occupied")
    void testAssignRunway(){
        Runway runway = new Runway("R-1", new Location(1500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        controlTower.assignRunway(runway);

        assertFalse(runway.isAvailable(), "Runway should be assigned to plane");
    }

    @Test
    @DisplayName("Should return true if runway released")
    void testReleaseRunway(){
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));
        controlTower.releaseRunway(runway);

        assertTrue(runway.isAvailable(), "Runway should be released after landing");
    }

    @Test
    @DisplayName("Should return true when runway is released after across final approach point")
    void testReleaseRunwayIdPlaneFinalAtApproach(){
        Plane plane = new Plane("X");
        plane.getNavigator().setLocation(new Location(-3000, 1000, 700));
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, runway);

        assertTrue(runway.isAvailable(), "Runway should be released after take final approach point");
    }

    @Test
    @DisplayName("Should return true when no one plane in the space")
    void testRemovePlaneFromSpace(){
        Plane plane = new Plane("x");

        controlTower.registerPlane(plane);

        controlTower.removePlaneFromSpace(plane);

        assertTrue(!controlTower.getPlanes().contains(plane), "Plane should be removed from airspace");
    }

    @Test
    @DisplayName("Should return true when planes has landed")
    void testHasLandedOnRunway(){
        Plane plane1 = new Plane("x");
        plane1.getNavigator().setLocation(new Location(500, 1000, 0));
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        assertTrue(controlTower.hasLandedOnRunway(plane1, runway), "Plane should be set as landed");
    }

}
