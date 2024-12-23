package unit_tests;

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
import org.mockito.Mockito;
import plane.Plane;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class RunwayUnitTest {
    AirportDatabase mockDatabase;
    PlaneDAO mockPlaneDAO;
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;
    Runway runway;

    @BeforeEach
    void setUp() throws SQLException {
        this.mockDatabase = Mockito.mock(AirportDatabase.class);
        this.mockPlaneDAO = Mockito.mock(PlaneDAO.class);
        this.mockCollisionDAO = Mockito.mock(CollisionDAO.class);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDAO);

        this.controlTower = new ControlTower(mockDatabase);
        this.runway = new Runway("R-1", new Location(500, 1000, 0), new Corridor("C-1", new Location(-5000, 3000, 1000), new Location(-3000, 1000, 700)));
    }

    @Test
    @DisplayName("Should return true when runway is available")
    void testIsRunwayAvailable(){
        assertTrue(controlTower.isRunwayAvailable(runway), "Runway should be set as available");
    }

    @Test
    @DisplayName("Should return false when runway is unavailable")
    void testIsRunwayUnavailable(){
        // Runway set as unavailable
        runway.setAvailable(false);
        assertFalse(controlTower.isRunwayAvailable(runway),"Runway should be set as unavailable" );
    }

    @Test
    @DisplayName("Should return false when runway is occupied")
    void testAssignRunway(){
        controlTower.assignRunway(runway);

        assertFalse(runway.isAvailable(), "Runway should be assigned to plane");
    }

    @Test
    @DisplayName("Should return true if runway released")
    void testReleaseRunway(){
        controlTower.releaseRunway(runway);

        assertTrue(runway.isAvailable(), "Runway should be released after landing");
    }

    @Test
    @DisplayName("Should return true when runway is released after across final approach point")
    void testReleaseRunwayIdPlaneFinalAtApproach(){
        Plane plane = new Plane("0000");
        plane.getNavigator().setLocation(new Location(-3000, 1000, 700));

        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, runway);

        assertTrue(runway.isAvailable(), "Runway should be released after take final approach point");
    }
}
