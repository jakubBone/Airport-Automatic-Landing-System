package unit_tests.database;

import com.jakub.bone.database.AirportDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionUnitTest {
    AirportDatabase database;

    @BeforeEach
    void setUp() throws SQLException {
        this.database = new AirportDatabase();
    }

    @Test
    @DisplayName("Database connection should not return be null")
    void testGetConnection() throws SQLException {
        assertNotNull(database.getDatabaseConnection(), "Database connection should not return null");
    }

    @Test
    @DisplayName("Database connection should close properly")
    void testCloseConnection() throws SQLException {
        database.closeConnection();
        assertTrue(database.getConnection().isClosed(), "Database connection should be closed");
    }
}
