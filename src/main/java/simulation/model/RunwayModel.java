package simulation.model;

import airport.Runway;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class RunwayModel {
    private Rectangle runwayRect;

    public RunwayModel(Runway runway) {
        this.runwayRect = new Rectangle(runway.getWidth() / 2, runway.getHeight() / 2);
        this.runwayRect.setFill(Color.BLUE);

        this.runwayRect.getTransforms().add(new Rotate(90, Rotate.X_AXIS));

        this.runwayRect.setTranslateX(0);
        this.runwayRect.setTranslateY(10);
        this.runwayRect.setTranslateZ(runway.getTouchdownPoint().getY() / 2);
    }

}