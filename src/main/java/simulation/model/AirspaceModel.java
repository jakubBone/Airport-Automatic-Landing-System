package simulation.model;

import airport.Airport;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
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
        this.floor = createWall(10000 / 2, 10000 / 2);
        this.leftWall = createWall(10000 / 2, 5000 / 2);
        this.rightWall = createWall(5000 / 2, 10000 / 2);
        setupFloor();
        setupLeftWall();
        setupRightWall();
    }

    private Rectangle createWall(int width, int height) {
        Rectangle wall = new Rectangle(width, height);
        Canvas canvas = new Canvas(width / 2, height / 2);
        int spacing = 500;

        addGrid(canvas, width, height, spacing);

        Image image = canvas.snapshot(null, null);
        wall.setFill(new ImagePattern(image));
        return wall;
    }

    private void addGrid(Canvas canvas, int width, int height, int spacing){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.GREEN);

        for (int y = 0; y <= height; y += spacing / 2) {
            gc.strokeLine(0, y, width, y);
        }

        for (int x = 0; x <= width; x += spacing / 2) {
            gc.strokeLine(x, 0, x, height);
        }
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

    private void setupRightWall() {
        this.rightWall.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        this.rightWall.getTransforms().add(new Rotate(270, Rotate.Y_AXIS));
        this.rightWall.setTranslateX(0);
        this.rightWall.setTranslateY(0);
        this.rightWall.setTranslateZ(-2500);
    }

}
