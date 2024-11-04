package simulation;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import lombok.Getter;

@Getter
public class Camera {
    private PerspectiveCamera camera;
    public Camera() {
        this.camera = new PerspectiveCamera();
        this.camera.setTranslateX(8000);
        this.camera.setTranslateY(-2500);
        this.camera.setTranslateZ(-12000);

        this.camera.getTransforms().add(new Rotate(-25, Rotate.Y_AXIS));
    }
}