package com.jakub.bone.core;

import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.ui.utills.SceneRenderer;
import javafx.application.Application;
import javafx.stage.Stage;
import com.jakub.bone.server.AirportServer;
import lombok.extern.log4j.Log4j2;

import java.sql.SQLException;

@Log4j2
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


