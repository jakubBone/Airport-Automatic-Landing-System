package com.jakub.bone.service;

import com.jakub.bone.client.PlaneClient;
import com.jakub.bone.config.ConfigLoader;
import com.jakub.bone.server.AirportServer;
import lombok.Getter;

import java.io.IOException;

import static com.jakub.bone.config.Constant.CLIENT_SPAWN_DELAY;
import static com.jakub.bone.config.Constant.SERVER_INIT_DELAY;

/*
 * The class manages the startup of the AirportServer
 * Initializes of PlaneClient instances to simulate air traffic
 * Spawns multiple client instances at defined intervals
 */
@Getter
public class AirportStateService {
    private AirportServer airportServer;

    public AirportStateService(AirportServer airportServer) {
        this.airportServer = airportServer;
    }

    public void startAirport() {
        if (airportServer.isRunning()) {
            return;
        }

        int serverPort = ConfigLoader.getInt("server.port");
        int maxClients = ConfigLoader.getInt("server.max-clients");
        String serverHost = ConfigLoader.get("database.host"); // localhost for local connections

        Thread serverThread = new Thread(() -> {
            try {
                this.airportServer.startServer(serverPort);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
            }
        });
        serverThread.start();

        // Wait for the server to initialize before proceeding
        while (airportServer == null || airportServer.getControlTowerService() == null) {
            try {
                Thread.sleep(SERVER_INIT_DELAY);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        new Thread(() -> {
            for (int i = 0; i < maxClients; i++) {
                PlaneClient client = new PlaneClient("localhost", serverPort);
                new Thread(client).start();

                try {
                    Thread.sleep(CLIENT_SPAWN_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
