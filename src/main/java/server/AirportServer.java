package server;

import controller.ControlTower;
import airport.Airport;
import controller.CollisionDetector;
import database.AirportDatabase;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import controller.PlaneHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

@Log4j2
@Getter
public class AirportServer  {
    private ServerSocket serverSocket;
    private ControlTower controlTower;
    private Airport airport;

    public AirportServer(ControlTower controller) throws SQLException {
        this.controlTower = controller;
        this.airport = new Airport();
    }

    public void startServer(int port) throws IOException {
        try {
            this.serverSocket = new ServerSocket(port);
            log.info("Server started");

            new CollisionDetector(controlTower).start();
            log.info("Collision detector started");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        log.info("Server connected with client at port: {}", port);
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
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                log.info("Server closed successfully");
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing server socket: {}", ex.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        AirportDatabase database = new AirportDatabase();
        ControlTower controlTower = new ControlTower(database);
        AirportServer airportServer = new AirportServer(controlTower);
        airportServer.startServer(5000);
        airportServer.stopServer();
    }
}
