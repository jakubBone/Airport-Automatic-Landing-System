package simulation.model;

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
        createPlane(plane);
        createLabel(plane);
    }

    public void createPlane(Plane plane) {
        PhongMaterial material = new PhongMaterial(Color.WHITE);

        this.planeSphere = new Sphere(100);
        this.planeSphere.setMaterial(material);
        this.planeSphere.setTranslateX(plane.getLocation().getX() / 2);
        this.planeSphere.setTranslateY(plane.getLocation().getY() / 2);
        this.planeSphere.setTranslateZ(plane.getLocation().getAltitude() / 2);
    }

    public void createLabel(Plane plane){
        this.label = new Text(Integer.toString(plane.getId()));
        this.label.setFont(new Font(120));
        this.label.setFill(Color.BLACK);

        this.label.setTranslateX((plane.getLocation().getX() / 2) + 100);
        this.label.setTranslateY((plane.getLocation().getY() / 2) - 100);
        this.label.setTranslateZ(plane.getLocation().getAltitude() / 2);
    }
}
