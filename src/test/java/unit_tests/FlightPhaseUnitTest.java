package unit_tests;

import airport.Airport;

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
import org.mockito.Mockito;
import plane.Plane;
import utills.Messenger;

import java.io.IOException;
import java.sql.SQLException;

import static airport.Airport.runway1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static plane.Plane.FlightPhase.*;

public class FlightPhaseUnitTest {
    AirportDatabase mockDatabase;
    PlaneDAO mockPlaneDAO;
    CollisionDAO mockCollisionDAO;
    ControlTower controlTower;
    FlightPhaseManager flightPhaseManager;
    Airport airport;
    Messenger messenger;


    @BeforeEach
    void setUp() throws SQLException {
        this.mockDatabase = Mockito.mock(AirportDatabase.class);
        this.mockPlaneDAO = Mockito.mock(PlaneDAO.class);
        this.mockCollisionDAO = Mockito.mock(CollisionDAO.class);
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
        Plane plane = new Plane("0000");

        Location descentPoint = new Location(0, 0, 300); // Holding altitude
        flightPhaseManager.processFlightPhase(plane, descentPoint, null);

        assertEquals(DESCENDING, plane.getPhase());
    }


    @Test
    @DisplayName("Should test flight phase switch to holding")
    void testPhaseSwitchToHolding() throws IOException, ClassNotFoundException {
        Plane plane = new Plane("0000");
        plane.getNavigator().setCurrentIndex(WaypointGenerator.generateDescentWaypoints().size());
        plane.descend();

        Location holdingPoint = new Location(0, 0, 1000); // Holding altitude
        flightPhaseManager.processFlightPhase(plane, holdingPoint, null);

        assertEquals(HOLDING, plane.getPhase());
    }

    @Test
    @DisplayName("Should test flight phase switch to landing when plane is at corridor entry point")
    void testPhaseSwitchToLandingAtCorridorEntryPoints() throws IOException, ClassNotFoundException {
        Plane plane = new Plane("0000");
        plane.getNavigator().setWaypoints(WaypointGenerator.getHoldingPatternWaypoints());
        plane.setPhase(HOLDING);

        Location corridorEntry = runway1.getCorridor().getEntryPoint();
        flightPhaseManager.processFlightPhase(plane, corridorEntry, null);

        assertEquals(LANDING, plane.getPhase(), "Plane should transition to LANDING phase at the corridor entry");
    }
}
