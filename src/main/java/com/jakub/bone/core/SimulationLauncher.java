package com.jakub.bone.core;

import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.ui.utills.SceneRenderer;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jakub.bone.server.AirportServer;

import java.sql.SQLException;

public class SimulationLauncher extends Application {
    private AirportServer airportServer;
    private AirportStateService airportStateService;
    private SceneRenderer visualization;

    @Override
    public void init() throws Exception {
        this.airportServer = new AirportServer();
        this.airportStateService = new AirportStateService(airportServer);
        this.visualization = new SceneRenderer(airportServer.getControlTowerService());
    }

    @Override
    public void start(Stage primaryStage) throws Exception, SQLException {
        airportStateService.startAirport();
        visualization.start(primaryStage);
    }

    @Override
    public void stop() {
        airportServer.stopServer();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


