package simulation;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import plane.Plane;

public class PlaneView extends Sphere {
    private Plane planeModel;
    private Airspace3D airspace3D;
    private PhongMaterial material;

    public PlaneView(Plane planeModel) {
        super(10); // plane ball shape size
        this.planeModel = planeModel;
        this.airspace3D = new Airspace3D();
        this.material = new PhongMaterial(Color.WHITE);
        this.setMaterial(material);
        updatePosition();
    }

    public void updatePosition() {
        Point3D scaledPosition = airspace3D.convertToDisplayScale(planeModel.getLocation());
        this.setTranslateX(scaledPosition.getX());
        this.setTranslateY(scaledPosition.getY());
        this.setTranslateZ(scaledPosition.getZ());
    }
}
