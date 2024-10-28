package simulation;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class AirspaceModel {
    public Box createAirspace(){
        Box airspace = new Box(400, 400, 400);
        PhongMaterial airspaceMaterial = new PhongMaterial();
        airspaceMaterial.setDiffuseColor(Color.BLACK);
        airspace.setMaterial(airspaceMaterial);
        airspace.setTranslateY(150);
        airspace.setTranslateZ(150);
        return airspace;
    }
}
