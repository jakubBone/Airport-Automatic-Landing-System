package unit_tests;

import controller.CollisionDetector;
import controller.ControlTower;
import database.AirportDatabase;
import location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import plane.Plane;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollisionUnitTest {
    AirportDatabase database;
    ControlTower controlTower;

    @BeforeEach
    void setUp() throws SQLException {
        this.database = new AirportDatabase();
        this.controlTower = new ControlTower(database);

    }

    @Test
    @DisplayName("Should test collision registration if distance between planes equal 10 meters")
    void tesPlanesCollisionAtNearLocalisation(){
        Plane plane1 = new Plane("0000");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4010));
        Plane plane2 = new Plane("0000");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertTrue(plane1.isDestroyed(), "Plane 1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "Plane 1 should be destroyed after collision");
    }

    @Test
    @DisplayName("Should test collision registration if distance between planes less than 10 meters")
    void tesPlanesCollisionAtTheSameLocalisation(){
        Plane plane1 = new Plane("0000");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4010));
        Plane plane2 = new Plane("0000");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertTrue(plane1.isDestroyed(), "Plane 1 should be destroyed after collision");
        assertTrue(plane2.isDestroyed(), "Plane 1 should be destroyed after collision");
    }

    @Test
    @DisplayName("Should test registration collision avoiding if planes beyond risk zone")
    void tesPlanesCollisionBeyondRiskZone(){
        Plane plane1 = new Plane("0000");
        plane1.getNavigator().setLocation(new Location(5000, 5000, 4020));
        Plane plane2 = new Plane("0000");
        plane2.getNavigator().setLocation(new Location(5000, 5000, 4000));

        controlTower.registerPlane(plane1);
        controlTower.registerPlane(plane2);

        controlTower.checkCollision();

        assertFalse(plane1.isDestroyed(), "Plane 1 should be destroyed after collision");
        assertFalse(plane2.isDestroyed(), "Plane 2 should be destroyed after collision");
    }
}
