package animation.utills;

import javafx.scene.PerspectiveCamera;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import lombok.Getter;

@Getter
public class Camera {
    private PerspectiveCamera camera;
    private final Rotate rotateX;
    private final Rotate rotateY;
    private final Translate translate;

    public Camera() {
        this.camera = new PerspectiveCamera();
        this.translate = new Translate(-4000, -2500, -20000);
        this.rotateX = new Rotate(25, Rotate.X_AXIS);
        this.rotateY = new Rotate(0, Rotate.Y_AXIS);

        // Apply transformations
        this.camera.getTransforms().addAll(translate, rotateX, rotateY);
    }

    public void rotate(double angleX, double angleY) {
        rotateX.setAngle(rotateX.getAngle() + angleX);
        rotateY.setAngle(rotateY.getAngle() + angleY);
    }

    public void zoom(double deltaZ) {
        translate.setZ(translate.getZ() + deltaZ);
    }

    public void pan(double deltaX, double deltaY) {
        translate.setX(translate.getX() + deltaX);
        translate.setY(translate.getY() + deltaY);
    }
}