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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneUpdater {
    private final Group root;
    private AirTrafficController controller;
    private final Map<String, PlaneModel> planeMap;

    public SceneUpdater(Group root, AirTrafficController controller) {
        this.root = root;
        this.controller = controller;
        this.planeMap =  new HashMap<>();
    }

    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> planes = controller.getPlanes();

        for(Plane plane: planes){
            if (!planeMap.containsKey(plane.getFlightNumber())) {
                PlaneModel planeModel = new PlaneModel(plane);
                planeMap.put(plane.getFlightNumber(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());

            }

            PlaneModel planeModel = planeMap.get(plane.getFlightNumber());

            if(plane.getPhase().equals(Plane.FlightPhase.HOLDING)){
                planeModel.setPlaneColour(Color.ORANGE);
            } else if(plane.getPhase().equals(Plane.FlightPhase.LANDING)){
                planeModel.setPlaneColour(Color.YELLOW);
            }

            if (!planeMap.containsKey(plane.getFlightNumber())) {
                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            }

            Location nextWaypoint = plane.getNavigator().getLocation();
            planeModel.animateToNextWaypoint(nextWaypoint);
        }

        for(Plane plane: planes){
            planeMap.entrySet().removeIf(entry -> {
                String flightNumber = entry.getKey();
                PlaneModel planeModel = entry.getValue();

                // If the flight number is no longer in activePlanes, remove the model from the scene
                if (!planeMap.containsKey(flightNumber)) {
                    root.getChildren().removeAll(planeModel.getPlaneSphere(), planeModel.getLabel());
                    return true;
                }
                return false;
            });
        }



        /*for(Plane plane: planes){
            if (!planeMap.containsKey(plane.getFlightNumber())) {
                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            }
        }*/



        /*if (!planeMap.containsKey(plane.getFlightNumber())) {
            PlaneModel planeModel = new PlaneModel(plane);
            planeMap.put(plane.getFlightNumber(), planeModel);
            root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());

        }

        planeMap.entrySet().removeIf(entry -> {
            Plane plane = controller.getPlaneByFlightNumber(entry.getKey());
            return plane == null || plane.isDestroyed() || plane.isLanded();
        });*/




        /////////////////////////////

        /*// Create a map of active planes for easier lookup by flight number
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
        }*/
    }


}
