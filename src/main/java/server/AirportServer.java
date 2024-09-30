package server;

import airport.AirTrafficController;
import airport.Airport;
import lombok.extern.log4j.Log4j2;
import handler.PlaneHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


@Log4j2
public class AirportServer  {
    private ServerSocket serverSocket;
    private AirTrafficController controller;
    private Airport airport;

    public AirportServer() {
        this.controller = new AirTrafficController();
        this.airport = new Airport();
    }

    public void startServer(int port) throws IOException {
        AirTrafficController controller = new AirTrafficController();
        Airport airport = new Airport();

        try {
            this.serverSocket = new ServerSocket(port);
            log.info("Server started");
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        log.info("Server connected with client at port: {}", port);
                        new PlaneHandler(clientSocket, controller, airport).start();
                    }
                } catch (Exception ex) {
                    log.error("Error occurred: {}", ex.getMessage());
                }
            }
        } catch (IOException ex){
            log.error("Failed to start server on port {}: {}", port, ex.getMessage());
        }
    }

    public void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing server socket: {}", ex.getMessage());
        }
    }

    public static void main(String[] args) throws IOException{
        AirportServer airportServer = new AirportServer();
        airportServer.startServer(5000);
    }
}
