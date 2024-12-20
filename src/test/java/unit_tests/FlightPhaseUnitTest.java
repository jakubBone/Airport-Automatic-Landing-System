package unit_tests;

import airport.Airport;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import plane.Plane;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static plane.Plane.FlightPhase.*;

public class FlightPhaseUnitTest {

    Airport airport;

    @BeforeEach
    void setUp()  {
        this.airport = new Airport();
    }

    @Test
    void testFlightPhaseChanges() {
        Plane plane = new Plane("0000");
        assertEquals(DESCENDING, plane.getPhase());

        plane.hold();
        assertEquals(HOLDING, plane.getPhase());

        plane.setLandingPhase(Airport.runway1);
        assertEquals(LANDING, plane.getPhase());
    }
}
