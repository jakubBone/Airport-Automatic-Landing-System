package animation;

import animation.utills.SceneRenderer;
import controller.AirTrafficController;
import client.PlaneClient;
import database.AirportDatabase;
import database.DatabaseSchema;
import javafx.application.Application;
import javafx.stage.Stage;
import server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

public class SimulationLauncher extends Application {
    private AirTrafficController controller;
    private AirportServer airportServer;
    private AirportDatabase database;
    @Override
    public void start(Stage primaryStage) throws Exception, SQLException {
        this.database = new AirportDatabase();
        this.controller = new AirTrafficController(database);
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

        int numberOfClients = 100;

        new Thread(() -> {
            for (int i = 0; i < numberOfClients; i++) {
                PlaneClient client = new PlaneClient("localhost", 5000);
                new Thread(client).start();
                try {
                    Thread.sleep(1000);
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


