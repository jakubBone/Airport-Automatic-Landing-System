package animation.utills;

import animation.model.PlaneModel;
import controller.AirTrafficController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import location.Location;
import plane.Plane;

import java.util.*;

public class SceneUpdater {
    private final Group root;
    private AirTrafficController controller;
    private Map<String, PlaneModel> planesMap;

    public SceneUpdater(Group root, AirTrafficController controller) {
        this.root = root;
        this.controller = controller;
        this.planesMap = new HashMap<>();
    }

    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> planes = controller.getPlanes();

        for (Plane plane : planes) {
            PlaneModel planeModel = new PlaneModel(plane);
            if (!planesMap.containsKey(plane.getFlightNumber())) {
                planesMap.put(plane.getFlightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneGroup(), planeModel.getLabel());
            }

            planeModel = planesMap.get(plane.getFlightNumber());

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

