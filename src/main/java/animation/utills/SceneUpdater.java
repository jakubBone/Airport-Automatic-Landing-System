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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SceneUpdater {
    private final Group root;
    private AirTrafficController controller;
    private Map<String, PlaneModel> planeMap;

    public SceneUpdater(Group root, AirTrafficController controller, Map<String, PlaneModel> planeMap) {
        this.root = root;
        this.controller = controller;
        this.planeMap = planeMap;
    }

    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> activePlanes = controller.getPlanes();

        // Create a map of active planes for easier lookup by flight number
        Map<String, Plane> activePlanesMap = activePlanes.stream()
                .collect(Collectors.toMap(Plane::getFlightNumber, plane -> plane));

        // Iterate through the existing plane models in planeMap
        planeMap.entrySet().removeIf(entry -> {
            String flightNumber = entry.getKey();
            PlaneModel planeModel = entry.getValue();

            // If the flight number is no longer in activePlanes, remove the model from the scene
            if (!activePlanesMap.containsKey(flightNumber)) {
                root.getChildren().removeAll(planeModel.getPlaneSphere(), planeModel.getLabel());
                return true; // Usuń z mapy planeMap
            }
            return false;
        });

        // Add new planes that do not already have models
        for (Plane plane : activePlanes) {
            if (!planeMap.containsKey(plane.getFlightNumber())) {
                PlaneModel planeModel = new PlaneModel(plane);
                planeMap.put(plane.getFlightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            }


            // Update the position of existing planes
            PlaneModel planeModel = planeMap.get(plane.getFlightNumber());

            if(plane.getPhase().equals(Plane.FlightPhase.HOLDING)){
                planeModel.setPlaneColour(Color.ORANGE);
            } else if(plane.getPhase().equals(Plane.FlightPhase.LANDING)){
                planeModel.setPlaneColour(Color.YELLOW);
            }
            Location nextWaypoint = plane.getNavigator().getLocation();
            planeModel.animateToNextWaypoint(nextWaypoint);
        }
    }

        //List<Plane> activePlanes = controller.getPlanes();
        /*for (Plane plane : activePlanes) {
            PlaneModel planeModel = planeMap.get(plane.getFlightNumber());

            if (planeModel == null) {
                planeModel = new PlaneModel(plane);
                planeMap.put(plane.getFlightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            }

            if(plane.getPhase().equals(Plane.FlightPhase.HOLDING)){
                planeModel.setPlaneColour(Color.ORANGE);
            } else if(plane.getPhase().equals(Plane.FlightPhase.LANDING)){
                planeModel.setPlaneColour(Color.YELLOW);
            }

            if (plane.isDestroyed()) {
                root.getChildren().removeAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            } else {
                Location nextWaypoint = plane.getNavigator().getLocation();
                planeModel.animateToNextWaypoint(nextWaypoint); // Animation smooth movement
            }

            if (plane.isLanded()) {
                PlaneModel finalPlaneModel = planeModel;
                new Timeline(new KeyFrame(Duration.millis(1000), ev -> {
                    root.getChildren().removeAll(finalPlaneModel.getPlaneSphere(), finalPlaneModel.getLabel());
                    planeMap.remove(plane.getFlightNumber());
                })).play();
            }
        }*/
}
