package simulation;

import airport.Airport;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class AirspaceModel {
    Airport airport;
    Rectangle floor;
    Rectangle leftWall;
    Rectangle rightWall;

    public AirspaceModel(Airport airport) {
        this.airport = airport;
        this.floor = createWall(airport.width, airport.depth);
        this.leftWall = createWall(airport.width, airport.depth);
        this.rightWall = createWall(airport.width, airport.depth);
        setupFloor();
    }

    private Rectangle createWall(int width, int height){
        Rectangle wall = new Rectangle(width / 2, height / 2, Color.BLACK);
        return createGriddedWall(wall);
    }

    public Rectangle createGriddedWall(Rectangle wall) {
        Canvas canvas = new Canvas(wall.getWidth(), wall.getHeight());
        GraphicsContext context2D = canvas.getGraphicsContext2D();

        drawGrid(wall, context2D);

        Image gridImage = canvas.snapshot(null, null);
        wall.setFill(new ImagePattern(gridImage));

        return wall;
    }

    private void drawGrid(Rectangle wall, GraphicsContext context2D){
        double gridSize = 100;
        context2D.setStroke(Color.WHITE);
        context2D.setLineWidth(1);

        for (double x = 0; x < wall.getWidth(); x += gridSize) {
            context2D.strokeLine(x, 0, x, wall.getHeight());
        }
        for (double y = 0; y < wall.getHeight(); y += gridSize) {
            context2D.strokeLine(0, y, wall.getWidth(), y);
        }
    }

    private void setupFloor() {
        floor.getTransforms().add(new Rotate(90, Rotate.X_AXIS));
        floor.setTranslateX(-floor.getWidth() / 2);
        floor.setTranslateY(floor.getHeight() / 2);
    }
    private void setupLeftWall() {
        leftWall.setTranslateX(-leftWall.getWidth() / 2);
        leftWall.setTranslateY(-leftWall.getHeight() / 4);
        leftWall.setTranslateZ(leftWall.getWidth());

    }

    private void setupRightWall() {
        rightWall.getTransforms().add(new Rotate(90, Rotate.Y_AXIS));
        rightWall.setTranslateX(rightWall.getHeight() / 2);
        rightWall.setTranslateY(-rightWall.getHeight() / 4);
        rightWall.setTranslateZ(rightWall.getWidth());

    }
}
