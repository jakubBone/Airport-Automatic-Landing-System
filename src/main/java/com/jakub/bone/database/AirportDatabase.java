package com.jakub.bone.database;

import com.jakub.bone.config.ConfigLoader;
import com.jakub.bone.repository.DatabaseSchema;
import com.jakub.bone.repository.PlaneRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import com.jakub.bone.repository.CollisionRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@Log4j2
public class AirportDatabase {
    private final String host = ConfigLoader.get("database.host");
    private final int port = ConfigLoader.getInt("database.port");
    private final String database = ConfigLoader.get("database.name");
    private final String user = ConfigLoader.get("database.user");
    private final String password = ConfigLoader.get("database.password");
    private final String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

    private final DSLContext CONTEXT;
    private final DatabaseSchema SCHEMA;
    private final PlaneRepository PLANE_REPOSITORY;
    private final CollisionRepository COLLISION_REPOSITORY;
    private Connection connection;

    public AirportDatabase() throws SQLException {
        this.connection = getDatabaseConnection();
        this.CONTEXT = DSL.using(connection);
        this.PLANE_REPOSITORY = new PlaneRepository(CONTEXT);
        this.COLLISION_REPOSITORY = new CollisionRepository(CONTEXT);
        this.SCHEMA = new DatabaseSchema(CONTEXT);
    }

    public Connection getDatabaseConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            log.info("Connection established successfully with database '{}' on {}:{}", database, host, port);
        } catch (SQLException ex) {
            log.error("Failed to establish connection to the database '{}'. Error: {}", database, ex.getMessage(), ex);
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Connection to database '{}' closed successfully.", database);
            } catch (SQLException ex) {
                log.error("Failed to close connection. Error: {}", ex.getMessage(), ex);
            }
        }
    }
}
