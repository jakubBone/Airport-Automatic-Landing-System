package animation.utills;

import controller.AirTrafficController;
import airport.Airport;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import animation.model.AirspaceModel;
import animation.model.PlaneModel;
import animation.model.RunwayModel;
import animation.model.WaypointModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneRenderer extends Application {
    private Group root;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Integer, PlaneModel> planeMap;

    public SceneRenderer(AirTrafficController controller) {
        this.root = new Group();
        this.airport = new Airport();
        this.controller = controller;
        this.planeMap = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, 800, 600, Color.BLACK);
        root.getTransforms().add(new Scale(0.5, 0.5, 0.5)); // Zmniejsza skalę całej sceny o połowę

        Camera camera = new Camera();
        scene.setCamera(camera.getCamera());

        // Key press event handling
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> camera.zoom(-200);   // Zoom in
                case S -> camera.zoom(200);    // Zoom out
                case A -> camera.pan(-200, 0); // Pan left
                case D -> camera.pan(200, 0);  // Pan right
                case Q -> camera.pan(0, -200); // Pan upward
                case E -> camera.pan(0, 200);  // Pan downward
                case UP -> camera.rotate(-5, 0);   // Rotate upward
                case DOWN -> camera.rotate(5, 0);  // Rotate downward
                case LEFT -> camera.rotate(0, -5); // Rotate left
                case RIGHT -> camera.rotate(0, 5); // Rotate right
            }
        });

        SceneUpdater updater = new SceneUpdater(root, controller, planeMap);

        setupStaticElements(scene, camera);

        updater.start();

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupStaticElements(Scene scene, Camera camera){


        RunwayModel runway1Model = new RunwayModel(Airport.runway1);
        RunwayModel runway2Model = new RunwayModel(Airport.runway2);
        AirspaceModel airspaceModel = new AirspaceModel();
        WaypointModel waypointModel = new WaypointModel();



        root.getChildren().addAll(airspaceModel.getFloor(), airspaceModel.getLeftWall(), airspaceModel.getRightWall(),
                runway1Model.getRunwayRect(), runway2Model.getRunwayRect());
        //addWaypointModels(waypointModel.getLandingR1Models());
        //addWaypointModels(waypointModel.getLandingR2Models());
        //addWaypointModels(waypointModel.getHoldingPatternModels());
        //addWaypointModels(waypointModel.getHoldingAlternativePatternModels());
        //addWaypointModels(waypointModel.getDescentModels());
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
