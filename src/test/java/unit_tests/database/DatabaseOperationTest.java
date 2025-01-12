package unit_tests.database;

import com.jakub.bone.application.CollisionDetector;
import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.application.ControlTower;
import com.jakub.bone.database.AirportDatabase;
import com.jakub.bone.database.CollisionRepository;
import com.jakub.bone.database.PlaneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.jakub.bone.domain.plane.Plane;

import java.sql.SQLException;

import static com.jakub.bone.domain.airport.Airport.runway1;
import static org.mockito.Mockito.*;
import static com.jakub.bone.utills.Constant.ENTRY_POINT_CORRIDOR_1;

class DatabaseOperationTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneRepository mockPlaneRepository;
    @Mock
    CollisionRepository mockCollisionRepository;
    @InjectMocks
    ControlTower controlTower;
    CollisionDetector collisionDetector;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneRepository);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionRepository);
        collisionDetector = new CollisionDetector(controlTower);
    }

    @Test
    @DisplayName("Plane registration should be saved to the database")
    void testPlaneRegisterToDatabase() throws SQLException {
        Plane plane = new Plane("TEST_PLANE");

        // Registration should trigger a DB operation
        controlTower.registerPlane(plane);

        verify(mockPlaneRepository, times(1)).registerPlaneInDB(plane);
    }

    @Test
    @DisplayName("Landing should be saved to the database")
    void testLandingRegisterToDatabase() throws SQLException {
        Airport airport = new Airport();
        Plane plane = new Plane("TEST_PLANE");

        // Place the plane on the runway's landing point
        plane.getNavigator().setLocation(runway1.getLandingPoint());
        // Inform control tower that plane has landed
        controlTower.hasLandedOnRunway(plane, runway1);

        verify(mockPlaneRepository, times(1)).registerLandingInDB(plane);
    }

    @Test
    @DisplayName("Collision should be saved to the database")
    void testCollisionRegisterToDatabase() throws SQLException {
        Plane plane1 = new Plane("TEST_PLANE_1");
        Plane plane2 = new Plane("TEST_PLANE_2");

        // Both planes share the same location => collision scenario
        plane1.getNavigator().setLocation(ENTRY_POINT_CORRIDOR_1);
        plane2.getNavigator().setLocation(ENTRY_POINT_CORRIDOR_1);

        controlTower.getPlanes().add(plane1);
        controlTower.getPlanes().add(plane2);

        collisionDetector.checkCollision();

        // Build the IDs array that the control tower will pass to the DAO
        String[] collidedIDs = {plane1.getFlightNumber(), plane2.getFlightNumber()};

        verify(mockCollisionRepository, times(1)).registerCollisionToDB(collidedIDs);
    }
}
