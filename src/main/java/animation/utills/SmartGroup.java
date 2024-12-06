package animation.utills;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;

public class SmartGroup extends Group {
    Rotate r;
    Transform t = r;

    public SmartGroup(double v, double v1, double v2) {
        this.getTransforms().add(new Scale(v, v1, v2));
    }

    public void rotateByX(int ang){
        r = new Rotate(ang, Rotate.X_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }

    public void rotateByY(int ang){
        r = new Rotate(ang, Rotate.Y_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }

    public void rotateByZ(int ang){
        r = new Rotate(ang, Rotate.Z_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }
}
