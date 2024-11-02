package simulation;

import airport.Runway;
import controller.AirTrafficController;
import airport.Airport;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import plane.Plane;

import java.util.HashMap;
import java.util.Map;

public class AirportVisualization extends Application {
    private Group root;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Plane, Sphere> planeMap;
    public AirportVisualization(AirTrafficController controller) {
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
        AirspaceModel airspaceModel = new AirspaceModel(airport);

        scene.setCamera(camera.getCamera());
        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();

        //root.getChildren().addAll(airspaceModel.getFloor(), runway1Model.getRunwayRect());
        root.getChildren().addAll(airspaceModel.getFloor(), runway1Model.getRunwayRect(), runway2Model.getRunwayRect());
    }


    public static void main (String[]args){
        launch(args);
    }
}
