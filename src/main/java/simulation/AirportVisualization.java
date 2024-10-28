package simulation;

import airport.AirTrafficController;
import airport.Airport;
import airport.Runway;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import location.Location;
import plane.Plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirportVisualization extends Application {
    private Group root;
    private Airport airport;
    private final AirTrafficController controller;
    private Map<Plane, PlaneView> planeViews;
    public AirportVisualization(AirTrafficController controller) {
        this.root = new Group();
        this.airport = new Airport();
        this.controller = controller;
        this.planeViews = new HashMap<>();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root, 800, 600, Color.SKYBLUE);
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateX(0);
        camera.setTranslateY(-100);
        camera.setTranslateZ(-100);
        scene.setCamera(camera);

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();

        drawRunways();

        // Test plane
        Plane testPlane = new Plane();
        testPlane.setLocation(new Location(5000, 5000, 2000));
        PlaneView testPlaneView = new PlaneView(testPlane);
        root.getChildren().add(testPlaneView);

        startUpdatingPlanes();
    }

    public void drawRunways(){
        Runway runway1 = Airport.runway1;
        Runway runway2 = Airport.runway2;

        Rectangle runway1Rect = new Rectangle(200, 20);
        Rectangle runway2Rect = new Rectangle(200, 20);
        runway1Rect.setFill(Color.DARKGRAY);
        runway2Rect.setFill(Color.DARKGRAY);

        Point3D position1 = new Airspace3D().convertToDisplayScale(runway1.getTouchdownPoint());
        Point3D position2 = new Airspace3D().convertToDisplayScale(runway2.getTouchdownPoint());

        runway1Rect.setTranslateX(position1.getX());
        runway1Rect.setTranslateY(position1.getY());
        runway2Rect.setTranslateX(position2.getX());
        runway2Rect.setTranslateY(position2.getY());

        root.getChildren().addAll(runway1Rect, runway2Rect);
    }
    public void startUpdatingPlanes() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updatePlanes()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    public void updatePlanes(){
        List<Plane> planes = new ArrayList<>();
        planes.add(new Plane());
        planes.get(0).setLocation(new Location(5000, 5000, 5000));
        PlaneView testPlaneView = new PlaneView(planes.get(0));
        root.getChildren().add(testPlaneView);



        /*// Adding new planes to View
        for (Plane plane : controller.getPlanes()) {
            if (!planeViews.containsKey(plane)) {  // check if plane is already in the space
                PlaneView planeView = new PlaneView(plane);
                planeViews.put(plane, planeView);
                root.getChildren().add(planeView);  // add new plane
            }
        }
        // Aktualizacja pozycji każdego samolotu
        for (Plane plane : controller.getPlanes()) {
            PlaneView planeView = planeViews.get(plane);
            if (planeView != null) {
                planeView.updatePosition();
            }
        }*/

        //planeViews.values().forEach(PlaneView::updatePosition); // update each plane position
    }

    public static void main (String[]args){
        launch(args);
    }
}
