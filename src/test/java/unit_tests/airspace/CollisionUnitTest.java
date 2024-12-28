package unit_tests.airspace;

import controller.ControlTower;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import plane.Plane;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CollisionUnitTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneDAO mockPlaneDAO;
    @Mock
    CollisionDAO mockCollisionDao;
    ControlTower controlTower;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDao);
        this.controlTower = new ControlTower(mockDatabase);
    }

    @Test
    @DisplayName("Should test collision registration if distance between planes less than 10 meters")
    void tesPlanesCollisionAtTheSameLocalisation(){
        Plane plane1 = new Plane("TEST_PLANE_1");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4010));
        Plane plane2 = new Plane("TEST_PLANE_2");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertTrue(plane1.isDestroyed(), "TEST_PLANE_1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "TEST_PLANE_2 should be destroyed after collision");
    }

    @Test
    @DisplayName("Should test collision registration if distance between planes equal 10 meters")
    void tesPlanesCollisionAtRiskZone(){
        Plane plane1 = new Plane("TEST_PLANE_1");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4010));
        Plane plane2 = new Plane("TEST_PLANE_2");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertTrue(plane1.isDestroyed(), "TEST_PLANE_1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "TEST_PLANE_2 should be destroyed after collision");
    }



    @Test
    @DisplayName("Should test registration collision avoiding if planes beyond risk zone")
    void tesPlanesCollisionBeyondRiskZone(){
        Plane plane1 = new Plane("TEST_PLANE_1");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4020));
        Plane plane2 = new Plane("TEST_PLANE_2");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertFalse(plane1.isDestroyed(), "TEST_PLANE_1 should not be destroyed after collision");
        assertFalse(plane2.isDestroyed(), "TEST_PLANE_2 should not be destroyed after collision");
    }

}