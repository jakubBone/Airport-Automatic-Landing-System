package animation.utills;

import controller.AirTrafficController;
import airport.Airport;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import animation.model.AirspaceModel;
import animation.model.PlaneModel;
import animation.model.RunwayModel;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import java.util.Map;

public class SceneRenderer extends Application {
    private SmartGroup group;
    private Scene scene;
    Camera camera;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Integer, PlaneModel> planeMap;

    public SceneRenderer(AirTrafficController controller) {
        this.group = new SmartGroup(0.5, 0.5, 0.5);
        this.scene = new Scene(group, 800, 600, Color.BLACK);
        this.camera = new Camera();
        this.airport = new Airport();
        this.controller = controller;
        this.planeMap = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene.setCamera(camera.getCamera());

        SceneUpdater updater = new SceneUpdater(group, controller, planeMap);

        setupStaticElements();
        setupRotationHandler();

        updater.start();

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupStaticElements(){
        RunwayModel runway1Model = new RunwayModel(Airport.runway1);
        RunwayModel runway2Model = new RunwayModel(Airport.runway2);
        AirspaceModel airspaceModel = new AirspaceModel();

        group.getChildren().addAll(airspaceModel.getFloor(), airspaceModel.getLeftWall(), airspaceModel.getRightWall(),
                runway1Model.getRunwayRect(), runway2Model.getRunwayRect());
    }

    private void setupRotationHandler(){
        camera.initializeRotationControls(group, scene);
    }

    public static void main (String[]args){
        launch(args);
    }

}
