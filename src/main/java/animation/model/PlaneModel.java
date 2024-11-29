package animation.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Getter;
import plane.Plane;

@Getter
public class PlaneModel {
    private Sphere planeSphere;
    private Text label;

    public PlaneModel(Plane plane) {
        createPlane();
        createLabel(plane);
        updatePosition(plane);
    }

    public void createPlane() {
        PhongMaterial material = new PhongMaterial(Color.WHITE);
        this.planeSphere = new Sphere(50);
        this.planeSphere.setMaterial(material);
    }

    public void createLabel(Plane plane) {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.MAGENTA);
    }

    public void updatePosition(Plane plane) {
        this.planeSphere.setTranslateX((plane.getLocation().getX()) / 2);
        this.planeSphere.setTranslateY(-(plane.getLocation().getAltitude()) / 2);
        this.planeSphere.setTranslateZ((plane.getLocation().getY()) / 2);

        this.label.setTranslateX(((plane.getLocation().getX() + 150)) / 2);
        this.label.setTranslateY(-((plane.getLocation().getAltitude() -150)) / 2);
        this.label.setTranslateZ((plane.getLocation().getY()) / 2);
    }
}
