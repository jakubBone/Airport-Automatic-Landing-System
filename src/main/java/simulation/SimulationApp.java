package simulation;

import javafx.application.Application;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import plane.Plane;

public class SimulationApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Plane plane = new Plane();
        PlaneController planeController = new PlaneController(plane);
        Pane root = new AnchorPane();
        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateX(100);
        camera.setTranslateY(100);
        camera.setTranslateZ(-100);


        Scene scene = new Scene(root, 800, 600, Color.WHITE);
        scene.setCamera(camera);

        primaryStage.setTitle("Airport Automatic Landing System");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}


