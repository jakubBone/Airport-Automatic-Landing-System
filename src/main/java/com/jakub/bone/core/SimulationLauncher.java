package com.jakub.bone.core;

import com.jakub.bone.ui.utills.SceneRenderer;
import com.jakub.bone.client.PlaneClient;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jakub.bone.server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

import static com.jakub.bone.utills.Constant.CLIENT_SPAWN_INTERVAL_DELAY;
import static com.jakub.bone.utills.Constant.SERVER_INIT_DELAY;

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
            Thread.sleep(SERVER_INIT_DELAY);
        }

        int clientsNumber = 10000;

        new Thread(() -> {
            for (int i = 0; i < clientsNumber; i++) {
                PlaneClient client = new PlaneClient("localhost", 5000);
                new Thread(client).start();

                try {
                    Thread.sleep(CLIENT_SPAWN_INTERVAL_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted", ex);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


