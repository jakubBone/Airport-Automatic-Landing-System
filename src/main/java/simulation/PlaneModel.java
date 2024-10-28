package simulation;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Text;
import plane.Plane;

public class PlaneModel {
    public Sphere createPlane(Plane plane) {
        Sphere planeModel = new Sphere(10);
        PhongMaterial material = new PhongMaterial(Color.WHITE);
        planeModel.setMaterial(material);
        planeModel.setTranslateX(plane.getLocation().getX());
        planeModel.setTranslateY(plane.getLocation().getY());
        planeModel.setTranslateZ(plane.getLocation().getAltitude());

        Text planeId = new Text(Integer.toString(plane.getId()));
        planeId.setTranslateX(plane.getLocation().getX());
        planeId.setTranslateY(plane.getLocation().getY() - 20);
        planeId.setTranslateZ(plane.getLocation().getAltitude());
        planeId.setFill(Color.BLACK);

        return planeModel;
    }
}
