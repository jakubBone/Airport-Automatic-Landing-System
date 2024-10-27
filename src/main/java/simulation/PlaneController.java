package simulation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import plane.Plane;

public class PlaneController {
    private PlaneView planeView;
    private Timeline timeline;

    public PlaneController(Plane plane) {
        this.planeView = new PlaneView(plane);
        this.timeline = new Timeline();
        this.timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updatePlane()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updatePlane(){
        planeView.updatePosition();
    }
}
