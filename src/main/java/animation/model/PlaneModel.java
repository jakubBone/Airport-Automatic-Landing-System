package animation.model;

import javafx.animation.Interpolator;
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
        plane.addObserver(this); // register as observer
        updatePosition(plane.getNavigator().getLocation());
    }

    public void createPlane(Plane plane) {
        this.plane = plane;
        this.material = new PhongMaterial(Color.WHITE);
        this.planeSphere = new Sphere(50);
        this.planeSphere.setMaterial(material);
    }

    public void createLabel() {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.BLACK);
        this.label.setText(plane.getFlightNumber()); // Ustawienie tekstu
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
        this.planeSphere.setTranslateX(location.getX() / 2.0);
        this.planeSphere.setTranslateY(-location.getAltitude() / 2.0);
        this.planeSphere.setTranslateZ(location.getY() / 2.0);
        this.label.setTranslateX(((location.getX() + 150)) / 2.0);
        this.label.setTranslateY(-((location.getAltitude() + 150)) / 2.0);
        this.label.setTranslateZ((location.getY()) / 2.0);
    }



    public void animateMovement(Location nextLocation) {
        double toPlaneSphereX = nextLocation.getX() / 2.0;
        double toPlaneSphereY = -nextLocation.getAltitude() / 2.0;
        double toPlaneSphereZ = nextLocation.getY() / 2.0;
        double toLabelX = (nextLocation.getX() + 150) / 2.0;
        double toLabelY =  - nextLocation.getAltitude() / 2.0;
        double toLabelZ = (nextLocation.getY() + 150) / 2.0;
        animatePlane(planeSphere, toPlaneSphereX, toPlaneSphereY, toPlaneSphereZ);
        animatePlane(label, toLabelX, toLabelY, toLabelZ);

    }

    public void animatePlane(Node node, double toX, double toY, double toZ) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition.setNode(node);
        transition.setToX(toX);
        transition.setToY(toY);
        transition.setToZ(toZ);

        transition.setInterpolator(Interpolator.LINEAR);

        transition.play();
    }

    private void removeFromScene(){
        planeSphere.setVisible(false);
    }
}
