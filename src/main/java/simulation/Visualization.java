package simulation;

import controller.AirTrafficController;
import airport.Airport;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.util.Duration;
import plane.Plane;
import simulation.model.AirspaceModel;
import simulation.model.PlaneModel;
import simulation.model.RunwayModel;
import simulation.model.WaypointModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Visualization extends Application {
    private Group root;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Integer, PlaneModel> planeMap;

    public Visualization(AirTrafficController controller) {
        this.root = new Group();
        this.airport = new Airport();
        this.controller = controller;
        this.planeMap = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        Camera camera = new Camera();

        setupSceneElements(scene, camera);

        startUpdatingAirspace();

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupSceneElements(Scene scene, Camera camera){
        scene.setCamera(camera.getCamera());

        RunwayModel runway1Model = new RunwayModel(Airport.runway1);
        RunwayModel runway2Model = new RunwayModel(Airport.runway2);
        AirspaceModel airspaceModel = new AirspaceModel();
        WaypointModel waypointModel = new WaypointModel();

        root.getChildren().addAll(airspaceModel.getFloor(), airspaceModel.getLeftWall(), airspaceModel.getRightWall(),
                runway1Model.getRunwayRect(), runway2Model.getRunwayRect());

        //addWaypointModels(waypointModel.getLandingR1Models());
        //addWaypointModels(waypointModel.getLandingR2Models());
        //addWaypointModels(waypointModel.getHoldingPatternModels());
        //addWaypointModels(waypointModel.getDescentModels());
    }

    public void startUpdatingAirspace() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> activePlanes = controller.getPlanes();

        for (Plane plane : activePlanes) {
            PlaneModel planeModel = planeMap.get(plane.getId());

            if (planeModel == null) {
                planeModel = new PlaneModel(plane);
                planeMap.put(plane.getId(), planeModel);

                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            }

            if(plane.isLanded() || plane.isDestroyed()){
               root.getChildren().removeAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            } else {
                planeModel.updatePosition(plane);
            }
        }
    }

    private void addWaypointModels(List<Sphere> waypointModels) {;
        for (Sphere model: waypointModels) {
            root.getChildren().add(model);
        }
    }

    public static void main (String[]args){
        launch(args);
    }
}
