package simulation;

import airport.Runway;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;

@Getter
public class RunwayModel {
    private Rectangle runwayRect;

    public RunwayModel() {
        this.runwayRect = new Rectangle(5000, 2500);
        this.runwayRect.setFill(Color.BLUE);

        //this.runwayRect.setRotationAxis(Rotate.X_AXIS);
        //this.runwayRect.setRotate(90);
        this.runwayRect.setTranslateX(-1000);
        this.runwayRect.setTranslateY(0);
        this.runwayRect.setTranslateZ(-10000);
    }
}
