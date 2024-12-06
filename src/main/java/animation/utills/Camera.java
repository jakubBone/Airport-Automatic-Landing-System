package animation.utills;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;

import javafx.scene.Scene;
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
        this.translate = new Translate(0, -5000, -15000);
        this.rotateX = new Rotate(25, Rotate.X_AXIS);
        this.rotateY = new Rotate(0, Rotate.Y_AXIS);
        this.camera.getTransforms().addAll(translate, rotateX, rotateY);
    }

    public void initializeRotationControls(Group group, Scene scene){
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

        group.getTransforms().addAll(rotateX, rotateY, rotateZ);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                // Camera
                case X -> zoom(-200);   // Zoom in
                case Z -> zoom(200);    // Zoom out
                case W -> rotateCamera(-5, 0);   // Upward
                case S -> rotateCamera(5, 0);  // Downward
                case D -> rotateCamera(0, -5); // Left
                case A -> rotateCamera(0, 5); // Right

                // Scene as Node
                case K -> rotateX.setAngle(rotateX.getAngle() + 10); // Rotation by X
                case RIGHT -> rotateY.setAngle(rotateY.getAngle() + 10); // Rotation by Y
                case UP -> rotateZ.setAngle(rotateZ.getAngle() + 10); // Rotation by Z
                case L -> rotateX.setAngle(rotateX.getAngle() - 10); // Rotation by X
                case LEFT -> rotateY.setAngle(rotateY.getAngle() - 10); // Rotation by Y
                case DOWN -> rotateZ.setAngle(rotateZ.getAngle() - 10); // Rotation by Z
            }
        });
    }

    public void rotateCamera(double angleX, double angleY) {
        rotateX.setAngle(rotateX.getAngle() + angleX);
        rotateY.setAngle(rotateY.getAngle() + angleY);
    }

    public void zoom(double deltaZ) {
        translate.setZ(translate.getZ() + deltaZ);
    }
}