package animation.utills;

import controller.AirTrafficController;
import airport.Airport;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
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
        Camera camera = new Camera();
        SceneUpdater updater = new SceneUpdater(root, controller, planeMap);

        setupStaticElements(scene, camera);

        updater.start();

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupStaticElements(Scene scene, Camera camera){
        scene.setCamera(camera.getCamera());

        RunwayModel runway1Model = new RunwayModel(Airport.runway1);
        RunwayModel runway2Model = new RunwayModel(Airport.runway2);
        AirspaceModel airspaceModel = new AirspaceModel();
        WaypointModel waypointModel = new WaypointModel();

        root.getChildren().addAll(airspaceModel.getFloor(), airspaceModel.getLeftWall(), airspaceModel.getRightWall(),
                runway1Model.getRunwayRect(), runway2Model.getRunwayRect(), airspaceModel.getBox());
        addWaypointModels(waypointModel.getLandingR1Models());
        addWaypointModels(waypointModel.getLandingR2Models());
        addWaypointModels(waypointModel.getHoldingPatternModels());
        addWaypointModels(waypointModel.getHoldingAlternativePatternModels());
        addWaypointModels(waypointModel.getDescentModels());
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
