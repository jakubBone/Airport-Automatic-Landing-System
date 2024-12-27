package unit_tests.airspace;

import controller.ControlTower;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import plane.Plane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    @DisplayName("Should test correct register planes")
    void testRegisterPlane(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane("0000"));
        }

        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        assertEquals(10, controlTower.getPlanes().size());
        assertTrue(controlTower.getPlanes().size() == 10, "All incoming planes should be registered");
    }

    @Test
    @DisplayName("Should test reporting when the airspace reaches maximum capacity")
        void testIsSpaceFull(){
        for (int i = 0; i < 110; i++){
            incomingPlanes.add(new Plane("0000"));
        }

        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        assertTrue(controlTower.isSpaceFull(), "Should report about maximum capacity");
    }

    @Test
    @DisplayName("Should test reporting when incoming plane reaches collision risk zone")
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
    @DisplayName("Should test correct plane remove")
    void testRemovePlaneFromSpace(){
        Plane plane = new Plane("0000");

        controlTower.registerPlane(plane);

        controlTower.removePlaneFromSpace(plane);

        assertFalse(controlTower.getPlanes().contains(plane), "Plane should be removed from airspace");
    }


}
