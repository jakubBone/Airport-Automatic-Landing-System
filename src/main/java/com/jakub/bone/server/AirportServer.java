package com.jakub.bone.server;

import com.jakub.bone.application.ControlTower;
import com.jakub.bone.domain.airport.Airport;
import com.jakub.bone.application.CollisionDetector;
import com.jakub.bone.database.AirportDatabase;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import com.jakub.bone.application.PlaneHandler;
import org.apache.logging.log4j.ThreadContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

@Log4j2
@Getter
@Setter
public class AirportServer  {
    private ServerSocket serverSocket;
    private AirportDatabase database;
    private ControlTower controlTower;
    private Airport airport;
    private boolean running;
    private boolean paused;

    public AirportServer() throws SQLException {
        this.database = new AirportDatabase();
        this.controlTower = new ControlTower(database);
        this.airport = new Airport();
        this.running = false;
        this.paused = false;
    }

    public void startServer(int port) throws IOException {
        ThreadContext.put("type", "Server");
        try {
            this.serverSocket = new ServerSocket(port);
            log.info("Server started");

            new CollisionDetector(controlTower).start();

            log.info("Collision detector started");

            while (true) {
                synchronized (this) {
                    while (paused) {
                        log.info("Airport paused. Waiting...");
                        wait();
                    }
                }

                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        log.debug("Server connected with client at port: {}", port);
                        running = true;
                        new PlaneHandler(clientSocket, controlTower, airport).start();
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
            throw new RuntimeException(e);
        } finally {
            stopServer();
            running = false;
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log.info("Server closed successfully");
            }

            if(database != null){
                database.closeConnection();
                log.info("Database closed successfully");
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing server socket: {}", ex.getMessage(), ex);
        }
    }

    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused) {
            notifyAll();
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        AirportServer airportServer = new AirportServer();
        airportServer.startServer(5000);
    }
}
