package simulation;

import airport.Airport;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class AirspaceModel {
    Airport airport;
    Rectangle floor;
    Rectangle leftWall;
    Rectangle rightWall;

    public AirspaceModel(Airport airport) {
        this.airport = airport;
        this.floor = createFloor(5000, 7000);
        setupFloor();
    }

    private Rectangle createFloor(int width, int depth) {
        Rectangle floor = new Rectangle(width , depth , Color.DARKGRAY);
        return floor;
    }

    private void setupFloor() {
        floor.setTranslateX(0);
        floor.setTranslateY(0);
        floor.setTranslateZ(0);

        //this.floor.setRotationAxis(Rotate.X_AXIS);
        //this.floor.setRotate(90);
    }
}
