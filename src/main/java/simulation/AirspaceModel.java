package simulation;

import airport.Airport;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class AirspaceModel {
    private Airport airport;
    private Rectangle floor;
    private Rectangle leftWall;
    private Rectangle rightWall;

    public AirspaceModel(Airport airport) {
        this.airport = airport;
        this.floor = createFloor(10000 / 2, 10000 / 2);
        this.leftWall = createFloor(10000 / 2, 10000 / 2);
        setupFloor();
        setupLeftWall();
    }

    private Rectangle createFloor(int width, int depth) {
        return new Rectangle(width, depth, Color.DARKGRAY);
    }

    private void setupFloor() {
        this.floor.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        this.floor.setTranslateX(0);
        this.floor.setTranslateY(0);
        this.floor.setTranslateZ(-2500);
    }

    private void setupLeftWall() {
        this.leftWall.getTransforms().add(new Rotate(180, Rotate.X_AXIS));
        this.leftWall.setTranslateX(0);
        this.leftWall.setTranslateY(0);
        this.leftWall.setTranslateZ(2500);
    }
}
