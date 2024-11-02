package simulation;

import airport.Runway;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class RunwayModel {
    private Rectangle runwayRect;

    public RunwayModel() {
        this.runwayRect = new Rectangle(5000, 5000);
        this.runwayRect.setFill(Color.BLUE);

        this.runwayRect.setRotationAxis(Rotate.X_AXIS);
        this.runwayRect.setRotate(90);
        this.runwayRect.setTranslateX(0);
        this.runwayRect.setTranslateY(100);
        this.runwayRect.setTranslateZ(0);

    }
}
