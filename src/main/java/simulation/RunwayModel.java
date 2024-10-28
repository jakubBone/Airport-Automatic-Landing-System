package simulation;

import airport.Runway;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class RunwayModel {
    public Rectangle createRunway(Runway runway) {
        Rectangle runwayRect = new Rectangle(200, 30);
        runwayRect.setFill(Color.DARKGRAY);
        runwayRect.setTranslateX(runway.getTouchdownPoint().getX());
        runwayRect.setTranslateY(runway.getTouchdownPoint().getY());
        runwayRect.setRotationAxis(Rotate.X_AXIS);
        runwayRect.setRotate(90);
        return runwayRect;
    }
}
