package simulation;

import controller.AirTrafficController;
import airport.Airport;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import location.Location;
import plane.Plane;
import simulation.model.AirspaceModel;
import simulation.model.PlaneModel;
import simulation.model.RunwayModel;

import java.util.HashMap;
import java.util.Map;

public class Visualization extends Application {
    private Group root;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Plane, Sphere> planeMap;
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
        RunwayModel runway1Model = new RunwayModel(Airport.runway1);
        RunwayModel runway2Model = new RunwayModel(Airport.runway2);
        AirspaceModel airspaceModel = new AirspaceModel();

        // Test plane
        Plane plane = new Plane();
        Location location = new Location(5000 , -2500 , -2500);
        plane.setLocation(location);
        PlaneModel planeModel = new PlaneModel(plane);

        scene.setCamera(camera.getCamera());
        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.getChildren().addAll(airspaceModel.getFloor(), airspaceModel.getLeftWall(), airspaceModel.getRightWall(),
                runway1Model.getRunwayRect(), runway2Model.getRunwayRect(),
                planeModel.getPlaneSphere(), planeModel.getLabel());
    }

    public static void main (String[]args){
        launch(args);
    }
}
