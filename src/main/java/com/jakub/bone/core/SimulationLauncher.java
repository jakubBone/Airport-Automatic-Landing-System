package com.jakub.bone.core;

import com.jakub.bone.ui.utills.SceneRenderer;
import com.jakub.bone.application.ControlTower;
import com.jakub.bone.client.PlaneClient;
import com.jakub.bone.database.AirportDatabase;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jakub.bone.server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

public class SimulationLauncher extends Application {
    private ControlTower controller;
    private AirportServer airportServer;
    private AirportDatabase database;
    @Override
    public void start(Stage primaryStage) throws Exception, SQLException {
        this.database = new AirportDatabase();
        this.controller = new ControlTower(database);
        this.airportServer = null;
        Thread serverThread = new Thread(() -> {
            try {
                airportServer = new AirportServer(controller);
                airportServer.startServer(5000);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        serverThread.isDaemon();
        serverThread.start();

        int clientsNumber = 1000;

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

        SceneRenderer visualization = new SceneRenderer(controller);
        visualization.start(primaryStage);

    }

    public static void main(String[] args) {
        launch(args);
    }
}


