package unit_tests.airspace;

import airport.Corridor;
import airport.Runway;
import controller.ControlTower;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import plane.Plane;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RunwayUnitTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneDAO mockPlaneDAO;
    @Mock
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;
    Runway runway;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDAO);

        this.controlTower = new ControlTower(mockDatabase);
        this.runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor("C-1", new Location(-5000, 3000, 1000), new Location(-3000, 1000, 700)));
    }

    @Test
    @DisplayName("Should test correct runway setting as available")
    void testIsRunwayAvailable(){
        assertTrue(controlTower.isRunwayAvailable(runway), "Runway should be set as available");
    }

    @Test
    @DisplayName("Should test correct runway setting as unavailable")
    void testIsRunwayUnavailable(){
        runway.setAvailable(false);
        assertFalse(controlTower.isRunwayAvailable(runway),"Runway should be set as unavailable" );
    }

    @Test
    @DisplayName("Should test runway assigning blocking when the runway is occupied")
    void testAssignRunway(){
        controlTower.assignRunway(runway);

        assertFalse(runway.isAvailable(), "Runway should be assigned to plane");
    }

    @Test
    @DisplayName("Should test correct runway release after landing")
    void testReleaseRunway(){
        controlTower.releaseRunway(runway);

        assertTrue(runway.isAvailable(), "Runway should be released after landing");
    }

    @Test
    @DisplayName("Should test runway release after across final approach point")
    void testReleaseRunwayIdPlaneFinalAtApproach(){
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(new Location(-3000, 1000, 700));

        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, runway);

        assertTrue(runway.isAvailable(), "Runway should be released after take final approach point by TEST_PLANE");
    }
}
