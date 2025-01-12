package com.jakub.bone.core;

import com.jakub.bone.ui.utills.SceneRenderer;
import com.jakub.bone.client.PlaneClient;
import com.jakub.bone.utills.Constant;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jakub.bone.server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

public class SimulationLauncher extends Application {
    private AirportServer airportServer;
    @Override
    public void start(Stage primaryStage) throws Exception, SQLException {
        this.airportServer = null;
        Thread serverThread = new Thread(() -> {
            try {
                airportServer = new AirportServer();
                airportServer.startServer(5000);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.start();

        // Wait for the server to initialize before proceeding
        while (airportServer == null || airportServer.getControlTower() == null) {
            Thread.sleep(Constant.SERVER_INIT_DELAY);
        }

        int clientsNumber = 10000;

        new Thread(() -> {
            for (int i = 0; i < clientsNumber; i++) {
                PlaneClient client = new PlaneClient("localhost", 5000);
                new Thread(client).start();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        SceneRenderer visualization = new SceneRenderer(airportServer.getControlTower());
        visualization.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


