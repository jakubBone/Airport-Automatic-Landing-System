package database;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Log4j2
public class AirportDatabase {
    private final String USER = "airport";
    private final String PASSWORD = "plane123";
    private final String DATABASE = "airport_system";
    private final String URL = String.format("jdbc:postgresql://localhost:%d/%s", 5432, DATABASE);;
    private final DSLContext CONTEXT;
    private final DatabaseSchema SCHEMA;
    private final PlaneDAO PLANE_DAO;
    private final CollisionDAO COLLISION_DAO;
    private Connection connection;

    public AirportDatabase() throws SQLException {
        this.connection = getAirportConnection();
        this.CONTEXT = DSL.using(connection);
        this.PLANE_DAO = new PlaneDAO(CONTEXT);
        this.COLLISION_DAO = new CollisionDAO(CONTEXT);
        this.SCHEMA = new DatabaseSchema(CONTEXT);
    }

    public Connection getAirportConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            log.info("Connection established successfully with database '{}' on port {}", DATABASE, 5432);
        } catch (SQLException ex) {
            log.error("Failed to establish connection to the database '{}'. Error: {}", DATABASE, ex.getMessage(), ex);
            throw ex;
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Connection to database '{}' closed successfully.", DATABASE);
            } catch (SQLException ex) {
                log.error("Failed to close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }
}
