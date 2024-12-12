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
import utills.Observable;
import utills.Observer;

@Getter
public class PlaneModel implements Observer {

    private Plane plane;
    private Sphere planeSphere;
    private Text label;
    private PhongMaterial material;

    public PlaneModel(Plane plane) {
        createPlane(plane);
        createLabel();
        setPlaneColour(Color.WHITE);
        plane.addObserver(this); // register as observer
        updatePosition(plane.getNavigator().getLocation());
    }

    /*public PlaneModel(Plane plane) {
        createPlane()
        createLabel();
        updatePosition(plane);
    }
*/
    public void createPlane(Plane plane) {
        this.plane = plane;
        this.material = new PhongMaterial(Color.WHITE);
        this.planeSphere = new Sphere(50);
        this.planeSphere.setMaterial(material);
    }

    public void createLabel() {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.MAGENTA);
    }

    @Override
    public void update(Observable observable) {
        if (observable instanceof Plane) {
            Plane updatedPlane = (Plane) observable;
            if (updatedPlane.isDestroyed()) {
                this.removeFromScene();
            } else if (updatedPlane.isLanded()) {
                this.removeFromScene();
            } else {
                this.updatePosition(updatedPlane.getNavigator().getLocation());
            }
        }
    }

    public void setPlaneColour(Color colour) {
        this.material = new PhongMaterial(colour);
        this.planeSphere.setMaterial(material);
    }

    private void updatePosition(Location location) {
        planeSphere.setTranslateX(location.getX() / 2.0);
        planeSphere.setTranslateY(-location.getAltitude() / 2.0);
        planeSphere.setTranslateZ(location.getY() / 2.0);
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
            label.setTranslateY(toY + 75);
            label.setTranslateZ(toZ + 75);
        });
        transition.play();
    }

    private void removeFromScene(){
        planeSphere.setVisible(false);
    }
}
