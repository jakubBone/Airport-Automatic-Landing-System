package unit_tests.database;

import database.AirportDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseConnectionUnitTest {
    AirportDatabase database;

    @BeforeEach
    void setUp() throws SQLException {
        this.database = new AirportDatabase();

    }

    @Test
    @DisplayName("Should test database connection getting")
    void testGetConnection() throws SQLException {
        assertNotNull(database.getAirportConnection());
    }

    @Test
    @DisplayName("Should test database connection closing")
    void testCloseConnection() throws SQLException {
        database.closeConnection();
        assertTrue(database.getConnection().isClosed());
    }
}
