package unit_tests.database;

import airport.Airport;
import controller.ControlTower;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import plane.Plane;
import utills.Constant;

import java.sql.SQLException;

import static airport.Airport.runway1;
import static org.mockito.Mockito.*;

public class DatabaseOperationUnitTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneDAO mockPlaneDAO;
    @Mock
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        this.controlTower = new ControlTower(mockDatabase);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDAO);
    }


    @Test
    @DisplayName("Should test new plane database registration")
    void testPlaneRegisterToDatabase() throws SQLException {
        Plane plane = new Plane("TEST_PLANE");
        controlTower.registerPlane(plane);

        verify(mockPlaneDAO, times(1)).registerPlaneInDB(plane);
    }

    @Test
    @DisplayName("Should test database landing registration")
    void testLandingRegisterToDatabase() throws SQLException {
        Airport airport = new Airport();
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(runway1.getLandingPoint());
        controlTower.hasLandedOnRunway(plane, runway1);

        verify(mockPlaneDAO, times(1)).registerLandingInDB(plane);
    }

    @Test
    @DisplayName("Should test database landing registration")
    void testCollisionRegisterToDatabase() throws SQLException {
        Plane plane1 = new Plane("TEST_PLANE_1");
        Plane plane2 = new Plane("TEST_PLANE_2");
        plane1.getNavigator().setLocation(Constant.ENTRY_POINT_CORRIDOR_1);
        plane2.getNavigator().setLocation(Constant.ENTRY_POINT_CORRIDOR_1);
        controlTower.getPlanes().add(plane1);
        controlTower.getPlanes().add(plane2);

        String [] collidedIDs = new String[2];
        collidedIDs[0] = plane1.getFlightNumber();
        collidedIDs[1] = plane2.getFlightNumber();

        controlTower.checkCollision();

        verify(mockCollisionDAO, times(1)).registerCollisionToDB(collidedIDs);
    }
}
