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
        setPlaneModel(plane);
        setTestField(plane);
    }

    public void setPlaneModel(Plane plane) {
        this.planeSphere = new Sphere(10);
        PhongMaterial material = new PhongMaterial(Color.WHITE);
        planeSphere.setMaterial(material);
        planeSphere.setTranslateX(plane.getLocation().getX());
        planeSphere.setTranslateY(plane.getLocation().getY());
        planeSphere.setTranslateZ(plane.getLocation().getAltitude());
    }
    public void setTestField(Plane plane){
        Text planeId = new Text(Integer.toString(plane.getId()));
        planeId.setTranslateX(plane.getLocation().getX());
        planeId.setTranslateY(plane.getLocation().getY() - 20);
        planeId.setTranslateZ(plane.getLocation().getAltitude());
        planeId.setFill(Color.BLACK);
    }
}
