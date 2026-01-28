package unit_tests.database;

import com.jakub.bone.config.ConfigLoader;
import com.jakub.bone.database.AirportDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {
    Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%d/%s",
                ConfigLoader.get("database.host"),
                ConfigLoader.getInt("database.port"),
                ConfigLoader.get("database.name"));
        this.connection = DriverManager.getConnection(url,
                ConfigLoader.get("database.user"),
                ConfigLoader.get("database.password"));
    }

    @Test
    @DisplayName("Database connection should not be null")
    void testGetConnection() throws SQLException {
        assertNotNull(connection, "Database connection should not return null");
    }

    @Test
    @DisplayName("Database connection should close properly")
    void testCloseConnection() throws SQLException {
        connection.close();
        assertTrue(connection.isClosed(), "Database connection should be closed");
    }

    @Test
    @DisplayName("AirportDatabase should be created with valid connection")
    void testAirportDatabaseCreation() throws SQLException {
        AirportDatabase database = new AirportDatabase(connection);
        assertNotNull(database.getPlaneRepository(), "PlaneRepository should not be null");
        assertNotNull(database.getCollisionRepository(), "CollisionRepository should not be null");
    }
}
