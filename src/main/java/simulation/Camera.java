package simulation;

import javafx.scene.PerspectiveCamera;
import lombok.Getter;

@Getter
public class Camera {
    private PerspectiveCamera camera;
    public Camera() {
        this.camera = new PerspectiveCamera();
        this.camera.setTranslateX(0);
        this.camera.setTranslateY(0);
        this.camera.setTranslateZ(-10000);

        this.camera.setNearClip(0.1);
        this.camera.setFarClip(5000);
    }
}