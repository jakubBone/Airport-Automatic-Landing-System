package animation.utills;

import animation.model.TerminalModel;
import controller.ControlTower;
import airport.Airport;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import animation.model.AirspaceModel;
import animation.model.RunwayModel;

public class SceneRenderer extends Application {
    private SmartGroup group;
    private Scene scene;
    Camera camera;
    private Airport airport;
    private final ControlTower controller;

    public SceneRenderer(ControlTower controller) {
        this.group = new SmartGroup(0.5, 0.5, 0.5);
        this.scene = new Scene(group, 800, 600, Color.BLACK);
        this.camera = new Camera();
        this.airport = new Airport();
        this.controller = controller;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene.setCamera(camera.getCamera());

        SceneUpdater updater = new SceneUpdater(group, controller);

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
        //WaypointModel waypointModel = new WaypointModel();
        TerminalModel terminal1Model = new TerminalModel(-1000, 50, 2000);
        TerminalModel terminal2Model = new TerminalModel(1000, 50, 2000);

        group.getChildren().addAll(airspaceModel.getFloor(), airspaceModel.getLeftWall(), airspaceModel.getRightWall(),
                runway1Model.getRunwayRect(), runway2Model.getRunwayRect(), terminal1Model.getPlaneGroup(), terminal2Model.getPlaneGroup());

       /* for(Sphere waypoint: waypointModel.getDescentModels()){
            group.getChildren().add(waypoint);
        }*/
    }

    private void setupRotationHandler(){
        camera.initializeRotationControls(group, scene);
    }

    public static void main (String[]args){
        launch(args);
    }

}
