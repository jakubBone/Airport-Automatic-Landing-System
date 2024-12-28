package unit_tests.flight;

import airport.Airport;

import airport.Runway;
import controller.ControlTower;
import controller.FlightPhaseManager;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import location.Location;
import location.WaypointGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import plane.Plane;
import utills.Messenger;

import java.io.IOException;
import java.sql.SQLException;

import static airport.Airport.runway1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static plane.Plane.FlightPhase.*;
import static utills.Constant.FINAL_APPROACH_CORRIDOR_1;

class FlightPhaseUnitTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneDAO mockPlaneDAO;
    @Mock
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;
    FlightPhaseManager flightPhaseManager;
    Airport airport;
    Messenger messenger;


    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPLANE_DAO()).thenReturn(mockPlaneDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(mockCollisionDAO);
        this.controlTower = new ControlTower(mockDatabase);
        this.airport = new Airport();
        this.messenger = mock(Messenger.class);
        this.flightPhaseManager = new FlightPhaseManager(controlTower, airport, messenger);

    }

    @Test
    @DisplayName("Should test flight phase set as descending after plane spawn")
    void testPhaseSettingToDescending() throws IOException, ClassNotFoundException {
        Plane plane = new Plane("TEST_PLANE");

        Location descentPoint = new Location(0, 0, 3000); // spawn altitude
        flightPhaseManager.processFlightPhase(plane, descentPoint, null);

        assertEquals(DESCENDING, plane.getPhase(), "Flight phase should be set as DESCENDING");
    }


    @Test
    @DisplayName("Should test flight phase switch to holding")
    void testPhaseSwitchToHolding() throws IOException, ClassNotFoundException {
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setCurrentIndex(WaypointGenerator.generateDescentWaypoints().size());
        plane.descend();

        Location holdingPoint = new Location(0, 0, 1000); // Holding altitude
        flightPhaseManager.processFlightPhase(plane, holdingPoint, null);

        assertEquals(HOLDING, plane.getPhase(), "Flight phase should be switched to HOLDING");
    }

    @Test
    @DisplayName("Should test flight phase switch to landing when plane is at corridor entry point")
    void testPhaseSwitchToLandingAtCorridorEntryPoint() throws IOException, ClassNotFoundException {
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
        plane.setPhase(HOLDING);

        Location corridorEntry = runway1.getCorridor().getEntryPoint();
        flightPhaseManager.processFlightPhase(plane, corridorEntry, null);

        assertEquals(LANDING, plane.getPhase(), "Flight phase should be switched to LANDING");
    }

    @Test
    @DisplayName("Should test correct plane marking as landed after landing process")
    void testMarkingAsLanded(){
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(runway1.getLandingPoint());

        assertTrue(controlTower.hasLandedOnRunway(plane, runway1), "TEST_PLANE should be marked as landed");
    }

    @Test
    @DisplayName("Should test runway releasing after cross final approach point")
    void testRunwayReleaseAfterCrossFinalApproach() {
        Plane plane = new Plane("TEST_PLANE");
        plane.getNavigator().setLocation(FINAL_APPROACH_CORRIDOR_1);
        runway1.setAvailable(false);

        controlTower.releaseRunwayIfPlaneAtFinalApproach(plane, runway1);

        assertTrue(runway1.isAvailable(), "Runway should be set as available");
    }
}
