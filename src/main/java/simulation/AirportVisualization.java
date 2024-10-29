package simulation;

import controller.AirTrafficController;
import airport.Airport;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;
import javafx.util.Duration;
import plane.Plane;

import java.util.HashMap;
import java.util.Map;

public class AirportVisualization extends Application {
    private Group root;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Plane, Sphere> planeMap;
    private RunwayModel runwayModel;
    private PlaneModel planeModel;
    private AirspaceModel airspaceModel;
    public AirportVisualization(AirTrafficController controller) {
        this.root = new Group();
        this.airport = new Airport();
        this.controller = controller;
        this.runwayModel = new RunwayModel();
        this.airspaceModel = new AirspaceModel(airport);
        this.planeModel = new PlaneModel();
        this.planeMap = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, 800, 600, Color.SKYBLUE);
        PerspectiveCamera camera = new PerspectiveCamera();
        scene.setCamera(camera);

        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-4000);
        camera.setNearClip(0.1);
        camera.setFarClip(5000);

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();

        setRunway();
        setAirspace();

        startUpdatingAirspace();
    }

    public void setRunway(){
        Rectangle runway1Rect = runwayModel.createRunway(Airport.runway1);
        Rectangle runway2Rect = runwayModel.createRunway(Airport.runway2);
        root.getChildren().addAll(runway1Rect, runway2Rect);
    }

    public void setAirspace(){
        Rectangle floor = airspaceModel.floor;
        root.getChildren().add(floor);
        //Rectangle leftWall = airspaceModel.createLeftWall();
        //Rectangle rightWall = airspaceModel.createRightWall();
        //root.getChildren().addAll(floor,leftWall, rightWall);
    }

    public void setPlane(Plane plane){
        root.getChildren().add(planeModel.createPlane(plane));
    }

    public void startUpdatingAirspace() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void updateAirspace(){
        for (Plane plane : controller.getPlanes()) {
            if (!planeMap.containsKey(plane)) {
                Sphere model = planeModel.createPlane(plane);
                planeMap.put(plane, model);
                setPlane(plane);
            }
        }

        for (Plane plane : controller.getPlanes()) {
            Sphere planeModel = planeMap.get(plane);
            if (planeModel != null) {
                planeModel.setTranslateX(plane.getLocation().getX());
                planeModel.setTranslateY(plane.getLocation().getY());
                planeModel.setTranslateZ(plane.getLocation().getAltitude());

                Text planeIdText = (Text) root.getChildren().stream()
                        .filter(node -> node instanceof Text && ((Text) node).getText().equals(Integer.toString(plane.getId())))
                        .findFirst()
                        .orElse(null);
                if (planeIdText != null) {
                    planeIdText.setTranslateX(plane.getLocation().getX());
                    planeIdText.setTranslateY(plane.getLocation().getY() - 20);
                    planeIdText.setTranslateZ(plane.getLocation().getAltitude());
                }
            }
        }
    }

    public static void main (String[]args){
        launch(args);
    }
}
