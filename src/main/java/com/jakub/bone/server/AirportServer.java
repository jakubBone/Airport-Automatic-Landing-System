package com.jakub.bone.server;

import com.jakub.bone.config.ConfigLoader;
import com.jakub.bone.config.ServerConstants;
import com.jakub.bone.service.ControlTowerService;
import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.service.CollisionService;
import com.jakub.bone.database.AirportDatabase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.application.PlaneHandler;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
@Getter
@Setter
public class AirportServer  {
    private static final int CONNECTION_POOL_SIZE = 20;
    private static final int EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS = 30;

    private ServerSocket serverSocket;
    private Connection connection;
    private AirportDatabase database;
    private ControlTowerService controlTowerService;
    private Airport airport;
    private CollisionService collisionService;
    private ExecutorService connectionPool;
    private boolean running;
    private boolean paused;
    private Instant startTime;

    public AirportServer() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%d/%s",
                ConfigLoader.get("database.host"),
                ConfigLoader.getInt("database.port"),
                ConfigLoader.get("database.name"));
        this.connection = DriverManager.getConnection(url,
                ConfigLoader.get("database.user"),
                ConfigLoader.get("database.password"));
        this.database = new AirportDatabase(connection);
        this.controlTowerService = new ControlTowerService(database);
        this.airport = new Airport();
        this.running = false;
        this.paused = false;
    }

    public void startServer(int port) throws IOException {
        ThreadContext.put("type", "Server");
        running = true;
        try {
            this.serverSocket = new ServerSocket(port);
            this.startTime = Instant.now();
            this.connectionPool = Executors.newFixedThreadPool(CONNECTION_POOL_SIZE);
            log.info("Server started with connection pool size: {}", CONNECTION_POOL_SIZE);

            this.collisionService = new CollisionService(controlTowerService);
            collisionService.start();
            log.info("Collision detector started");

            while (running) {
                if(paused) {
                    Thread.sleep(2000);
                    log.info("Airport paused. Waiting...");
                    continue;
                }

                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        log.debug("Server connected with client at port: {}", port);
                        connectionPool.submit(new PlaneHandler(clientSocket, controlTowerService, airport));
                    }
                } catch (Exception ex) {
                    if(serverSocket.isClosed()){
                        return;
                    }
                    log.error("Error handling client connection: {}", ex.getMessage(), ex);
                }
            }
        } catch (IOException ex) {
            log.error("Failed to start AirportServer on port {}: {}", port, ex.getMessage(), ex);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Server interrupted while paused");
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (collisionService != null) {
                collisionService.shutdown();
                log.info("Collision service stopped");
            }

            if (connectionPool != null) {
                connectionPool.shutdown();
                try {
                    if (!connectionPool.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                        connectionPool.shutdownNow();
                        log.warn("Connection pool forced shutdown after timeout");
                    }
                } catch (InterruptedException e) {
                    connectionPool.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                log.info("Connection pool closed successfully");
            }

            if(connection != null){
                try {
                    connection.close();
                    log.info("Database connection closed successfully");
                } catch (SQLException ex) {
                    log.error("Error closing database connection: {}", ex.getMessage(), ex);
                }
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log.info("Server closed successfully");
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing server socket: {}", ex.getMessage(), ex);
        }
    }

    public void pauseServer() {
        this.paused = true;
    }

    public void resumeServer() {
        this.paused = false;
    }

    public Duration getUptime(){
        return Duration.between(getStartTime(),Instant.now());
    }

    public static void main(String[] args) throws IOException, SQLException {
        AirportServer airportServer = new AirportServer();
        airportServer.startServer(ServerConstants.PORT);
    }
}
