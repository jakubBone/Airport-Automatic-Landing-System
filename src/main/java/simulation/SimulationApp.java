package simulation;

import controller.AirTrafficController;
import client.PlaneClient;
import javafx.application.Application;
import javafx.stage.Stage;
import server.AirportServer;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationApp extends Application {
    private AirTrafficController controller;
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.controller = new AirTrafficController();

        // Start Server
        new Thread(() -> {
            AirportServer airportServer = new AirportServer(controller);
            try {
                airportServer.startServer(5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Start Clients
        int numberOfClients = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            executorService.execute(client);
        }
        executorService.shutdown();


        // Start Visualization
        Visualization visualization = new Visualization(controller);
        visualization.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


