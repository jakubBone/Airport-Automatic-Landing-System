package com.jakub.bone.ui.utills;

import com.jakub.bone.ui.model.PlaneModel;
import com.jakub.bone.application.ControlTower;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.domain.plane.Plane;

import java.util.*;

import static com.jakub.bone.domain.plane.Plane.FlightPhase.LANDING;

public class SceneUpdater {
    private final Group root;
    private ControlTower controller;
    private Map<String, PlaneModel> planesMap;
    private boolean isFirstPlane;

    public SceneUpdater(Group root, ControlTower controller) {
        this.root = root;
        this.controller = controller;
        this.planesMap = new HashMap<>();
        this.isFirstPlane = true;
    }

    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> planes = controller.getPlanes();

        for (Plane plane : planes) {
            if(isFirstPlane){
                try{
                    Thread.sleep(500);
                } catch (InterruptedException ex){
                    ex.getMessage();
                }
                isFirstPlane = false;
            }
            PlaneModel planeModel;
            if (!planesMap.containsKey(plane.getFlightNumber())) {
                planeModel = new PlaneModel(plane);
                planesMap.put(plane.getFlightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }

            planeModel = planesMap.get(plane.getFlightNumber());
            if(plane.getPhase().equals(LANDING)){
                planeModel.setPlaneModelColor(Color.YELLOW);
            }

            Location nextWaypoint = plane.getNavigator().getLocation();
            planeModel.animateMovement(nextWaypoint);
        }
        cleanupScene();
    }

    private void cleanupScene(){
        for (String flightNumber : planesMap.keySet()) {
            Plane plane = controller.getPlaneByFlightNumber(flightNumber);
            PlaneModel planeModel = planesMap.get(flightNumber);
            if (plane == null || plane.isDestroyed() || plane.isLanded()) {
                root.getChildren().removeAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }
        }
    }
}

