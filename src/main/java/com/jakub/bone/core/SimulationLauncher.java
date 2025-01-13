package com.jakub.bone.core;

import com.jakub.bone.ui.utills.SceneRenderer;
import com.jakub.bone.client.PlaneClient;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jakub.bone.server.AirportServer;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.sql.SQLException;

import static com.jakub.bone.utills.Constant.CLIENT_SPAWN_DELAY;
import static com.jakub.bone.utills.Constant.SERVER_INIT_DELAY;

@Log4j2
public class SimulationLauncher extends Application {
    private AirportServer airportServer;
    @Override
    public void start(Stage primaryStage) throws Exception, SQLException {
        this.airportServer = null;
        Thread serverThread = new Thread(() -> {
            try {
                airportServer = new AirportServer();
                airportServer.startServer(5000);
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to database issues", ex);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
            }
        });

        serverThread.start();

        // Wait for the server to initialize before proceeding
        while (airportServer == null || airportServer.getControlTower() == null) {
            try {
                Thread.sleep(SERVER_INIT_DELAY);
            } catch (InterruptedException ex) {
                log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                Thread.currentThread().interrupt();
            }
        }

        int clientsNumber = 10000;

        new Thread(() -> {
            for (int i = 0; i < clientsNumber; i++) {
                PlaneClient client = new PlaneClient("localhost", 5000);
                new Thread(client).start();

                try {
                    Thread.sleep(CLIENT_SPAWN_DELAY);
                } catch (InterruptedException ex) {
                    log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                    Thread.currentThread().interrupt();
                }

            }
        }).start();

        SceneRenderer visualization = new SceneRenderer(airportServer.getControlTower());
        visualization.start(primaryStage);
    }

    @Override
    public void stop() {
        try {
            airportServer.getDatabase().getSCHEMA().clearTables();
            System.out.println("Database cleared during application shutdown");
        } catch (Exception ex) {
            log.error("Error occurred while clearing the database: {}", ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


