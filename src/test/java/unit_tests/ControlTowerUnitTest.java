package unit_tests;

import controller.ControlTower;
import airport.Corridor;
import airport.Runway;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import plane.Plane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ControlTowerUnitTest {
    AirportDatabase mockDatabase;
    PlaneDAO mockPlaneDAO;
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;
    List<Plane> incomingPlanes;
    @BeforeEach
    void setUp() throws SQLException {
        this.mockDatabase = Mockito.mock(AirportDatabase.class);
        this.mockPlaneDAO = Mockito.mock(PlaneDAO.class);
        this.mockCollisionDAO = Mockito.mock(CollisionDAO.class);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDAO);

        this.controlTower = new ControlTower(mockDatabase);
        this.incomingPlanes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return true when all planes registered")
    void testRegisterPlane(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane("0000"));
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
            incomingPlanes.add(new Plane("0000"));
        }

        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        assertTrue(controlTower.isSpaceFull(), "Should inform about full airspace");
    }
    @Test
    void testIfPlaneAtCollisionRiskZone() {
        Plane plane1 = new Plane("0000");
        controlTower.registerPlane(plane1);
        int index = plane1.getNavigator().getCurrentIndex();

        Plane plane2 = new Plane("1111");
        plane2.getNavigator().setCurrentIndex(index + 1);

        assertTrue(controlTower.isAtCollisionRiskZone(plane2),
                "Plane 2 should be detected in Plane 1's collision risk zone");
    }



    @Test
    @DisplayName("Should return true when no one plane in the space")
    void testRemovePlaneFromSpace(){
        Plane plane = new Plane("0000");

        controlTower.registerPlane(plane);

        controlTower.removePlaneFromSpace(plane);

        assertTrue(!controlTower.getPlanes().contains(plane), "Plane should be removed from airspace");
    }

    @Test
    @DisplayName("Should return true when planes has landed")
    void testHasLandedOnRunway(){
        Plane plane1 = new Plane("0000");
        plane1.getNavigator().setLocation(new Location(500, 1000, 0));
        Runway runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor( "C-1", new Location(1000, 2000, 0), new Location(-3000, 1000, 700)));

        assertTrue(controlTower.hasLandedOnRunway(plane1, runway), "Plane should be set as landed");
    }

}
