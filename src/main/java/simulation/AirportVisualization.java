package simulation;

import airport.AirTrafficController;
import airport.Airport;
import airport.Runway;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import plane.Plane;

import java.util.HashMap;
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
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.SKYBLUE);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateX(100);
        camera.setTranslateY(100);
        camera.setTranslateZ(-100);
        scene.setCamera(camera);

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();

        drawRunways();
        startUpdatingPlanes();
    }

    public void drawRunways(){
        Runway runway1 = Airport.runway1;
        Runway runway2 = Airport.runway2;

        Rectangle runway1Rect = new Rectangle(runway1.getTouchdownPoint().getX(), runway1.getTouchdownPoint().getY(), 200, 100);
        Rectangle runway2Rect = new Rectangle(runway2.getTouchdownPoint().getX(), runway2.getTouchdownPoint().getY(), 200, 100);
        runway1Rect.setFill(Color.DARKGRAY);
        runway2Rect.setFill(Color.DARKGRAY);

        root.getChildren().addAll(runway1Rect, runway2Rect);
    }
    public void startUpdatingPlanes() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updatePlanes()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    public void updatePlanes(){
        // Adding new planes to View
        for (Plane plane : controller.getPlanes()) {
            if (!planeViews.containsKey(plane)) {  // check if plane is already in the space
                PlaneView planeView = new PlaneView(plane);
                planeViews.put(plane, planeView);
                root.getChildren().add(planeView);  // add new plane
            }
        }

        planeViews.values().forEach(PlaneView::updatePosition); // update each plane position
    }

    public static void main (String[]args){
        launch(args);
    }
}
