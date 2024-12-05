package animation.model;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import location.Location;
import lombok.Getter;
import plane.Plane;

@Getter
public class PlaneModel {
    private Sphere planeSphere;
    private Text label;
    private PhongMaterial material;

    public PlaneModel(Plane plane) {
        createPlane();
        createLabel(plane);
        updatePosition(plane);
    }

    public void createPlane() {
        this.material = new PhongMaterial(Color.WHITE);
        this.planeSphere = new Sphere(50);
        this.planeSphere.setMaterial(material);
    }

    public void setPlaneColour(Color colour) {
        this.material = new PhongMaterial(colour);
        this.planeSphere.setMaterial(material);
    }

    public void createLabel(Plane plane) {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.MAGENTA);
    }

    public void updatePosition(Plane plane) {
        this.planeSphere.setTranslateX((plane.getNavigator().getLocation().getX()) / 2.0);
        this.planeSphere.setTranslateY(-(plane.getNavigator().getLocation().getAltitude()) / 2.0);
        this.planeSphere.setTranslateZ((plane.getNavigator().getLocation().getY()) / 2.0);

        this.label.setTranslateX(((plane.getNavigator().getLocation().getX() + 150)) / 2.0);
        this.label.setTranslateY(-((plane.getNavigator().getLocation().getAltitude() -150)) / 2.0);
        this.label.setTranslateZ((plane.getNavigator().getLocation().getY()) / 2.0);
    }

    public void animateToNextWaypoint(Location nextLocation) {
        double toX = nextLocation.getX() / 2.0;
        double toY = -nextLocation.getAltitude() / 2.0;
        double toZ = nextLocation.getY() / 2.0;
        animatePlane(this.planeSphere, toX, toY, toZ);
    }

    public void animatePlane(Node plane, double toX, double toY, double toZ) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition.setNode(plane);
        transition.setToX(toX);
        transition.setToY(toY);
        transition.setToZ(toZ);
        transition.setOnFinished(event -> {
            label.setTranslateX(toX + 75);
            label.setTranslateY(toY - 75);
            label.setTranslateZ(toZ);
        });
        transition.play();
    }
}
