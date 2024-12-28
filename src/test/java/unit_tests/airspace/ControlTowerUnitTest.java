package unit_tests.airspace;

import controller.ControlTower;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import plane.Plane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ControlTowerUnitTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneDAO mockPlaneDAO;
    @Mock
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;
    List<Plane> incomingPlanes;
    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDAO);

        this.controlTower = new ControlTower(mockDatabase);
        this.incomingPlanes = new ArrayList<>();
    }

    @Test
    @DisplayName("Should test correct register planes")
    void testRegisterPlane(){
        for (int i = 0; i < 10; i++){
            incomingPlanes.add(new Plane("TEST_PLANE"));
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
            incomingPlanes.add(new Plane("TEST_PLANE"));
        }

        for(Plane incoming: incomingPlanes){
            controlTower.registerPlane(incoming);
        }

        assertTrue(controlTower.isSpaceFull(), "Should report about maximum capacity");
    }

    @Test
    @DisplayName("Should test reporting when incoming plane reaches collision risk zone")
    void testIfPlaneAtCollisionRiskZone() {
        Plane plane1 = new Plane("TEST_PLANE_1");
        controlTower.registerPlane(plane1);
        int index = plane1.getNavigator().getCurrentIndex();

        Plane plane2 = new Plane("TEST_PLANE_2");
        plane2.getNavigator().setCurrentIndex(index + 1);

        assertTrue(controlTower.isAtCollisionRiskZone(plane2),
                "TEST_PLANE_2 should be detected in TEST_PLANE_2 collision risk zone");
    }

    @Test
    @DisplayName("Should test correct plane remove")
    void testRemovePlaneFromSpace(){
        Plane plane = new Plane("TEST_PLANE");

        controlTower.registerPlane(plane);

        controlTower.removePlaneFromSpace(plane);

        assertFalse(controlTower.getPlanes().contains(plane), "TEST_PLANE should be removed from airspace");
    }
}
