package simulation;

import airport.Runway;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class RunwayModel {
    private Rectangle runwayRect;

    public RunwayModel(Runway runway) {
        this.runwayRect = new Rectangle(5000, 30);
        this.runwayRect.setFill(Color.BLUE);
        this.runwayRect.setTranslateX(runway.getTouchdownPoint().getX());
        this.runwayRect.setTranslateY(runway.getTouchdownPoint().getY());
        this.runwayRect.setRotationAxis(Rotate.X_AXIS);
        this.runwayRect.setRotate(90);
    }
}
