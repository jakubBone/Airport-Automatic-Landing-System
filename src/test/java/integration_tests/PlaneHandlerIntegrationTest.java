package integration_tests;

import airport.Airport;
import controller.ControlTower;
import controller.FlightPhaseManager;
import database.AirportDatabase;
import org.junit.jupiter.api.BeforeEach;
import utills.Messenger;


import static org.mockito.Mockito.mock;

public class PlaneHandlerIntegrationTest {
    AirportDatabase mockDatabase;
    ControlTower mockControlTower;
    Airport airport;
    Messenger messenger;
    FlightPhaseManager flightPhaseManager;

    @BeforeEach
    void setUp() {
        this.mockDatabase = mock(AirportDatabase.class);
        this.mockControlTower = mock(ControlTower.class);
        this.airport = new Airport();
        this.messenger = new Messenger();
        this.flightPhaseManager = new FlightPhaseManager(mockControlTower, airport, messenger);
    }

}
