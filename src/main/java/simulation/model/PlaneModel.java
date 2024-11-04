package simulation.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import lombok.Getter;
import plane.Plane;

@Getter
public class PlaneModel {
    private Sphere planeSphere;
    public PlaneModel(Plane plane) {
        createPlane(plane);
        setTestField(plane);
    }

    public void createPlane(Plane plane) {
        PhongMaterial material = new PhongMaterial(Color.WHITE);

        this.planeSphere = new Sphere(100);
        this.planeSphere.setMaterial(material);
        this.planeSphere.setTranslateX(plane.getLocation().getX() / 2);
        this.planeSphere.setTranslateY(plane.getLocation().getY() / 2);
        this.planeSphere.setTranslateZ(plane.getLocation().getAltitude() / 2);
    }
    public void setTestField(Plane plane){
        Text planeId = new Text(Integer.toString(plane.getId()));
        planeId.setTranslateX(plane.getLocation().getX());
        planeId.setTranslateY(plane.getLocation().getY() - 20);
        planeId.setTranslateZ(plane.getLocation().getAltitude());
        planeId.setFill(Color.BLACK);
    }
}
