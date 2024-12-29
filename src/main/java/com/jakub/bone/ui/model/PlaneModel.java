package com.jakub.bone.ui.model;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import com.jakub.bone.domain.airport.Location;
import lombok.Getter;
import com.jakub.bone.domain.plane.Plane;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;


@Getter
public class PlaneModel {
    private Plane plane;
    private Group planeGroup;
    private MeshView[] meshViews;
    private Text label;

    public PlaneModel(Plane plane) {
        this.plane = plane;
        this.planeGroup = new Group();
        loadPlaneModel();
        createLabel();
        updatePosition(plane.getNavigator().getLocation());
    }
    public void loadPlaneModel() {
        ObjModelImporter importer = new ObjModelImporter();
        importer.read(getClass().getResource("/models/boeing737/boeingModel.obj"));

        meshViews = importer.getImport();
        planeGroup.getChildren().addAll(meshViews);
    }

    public void createLabel() {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.WHITE);
        this.label.setText(plane.getFlightNumber());
    }


    private void updatePosition(Location location) {
        this.planeGroup.setTranslateX(location.getX() / 2.0);
        this.planeGroup.setTranslateY(-location.getAltitude() / 2.0);
        this.planeGroup.setTranslateZ(location.getY() / 2.0);

        this.label.setTranslateX(((location.getX() + 150)) / 2.0);
        this.label.setTranslateY(-((location.getAltitude() + 150)) / 2.0);
        this.label.setTranslateZ((location.getY()) / 2.0);
    }

    public void animateMovement(Location nextLocation) {
        double currentX = planeGroup.getTranslateX();
        double currentZ = planeGroup.getTranslateZ();

        double toPlaneX = nextLocation.getX() / 2.0;
        double toPlaneY = -nextLocation.getAltitude() / 2.0;
        double toPlaneZ = nextLocation.getY() / 2.0;

        calculateAndSetHeading(currentX, currentZ, toPlaneX, toPlaneZ);

        setInterpolation(planeGroup, toPlaneX, toPlaneY, toPlaneZ);
        setInterpolation(label, (nextLocation.getX() + 150) / 2.0, toPlaneY, (nextLocation.getY() + 150) / 2.0);
    }

    private void calculateAndSetHeading(double currentX, double currentZ, double targetX, double targetZ) {
        double deltaX = targetX - currentX;
        double deltaZ = targetZ - currentZ;

        double angleRadians = Math.atan2(deltaZ, deltaX);
        double angleDegrees = Math.toDegrees(angleRadians);

        double correctedAngle = angleDegrees + 90;

        Rotate headingRotate = new Rotate();
        headingRotate.setAxis(Rotate.Y_AXIS);
        headingRotate.setAngle(-correctedAngle);

        planeGroup.getTransforms().clear();
        planeGroup.getTransforms().add(headingRotate);
    }

    public void setInterpolation(Node node, double toX, double toY, double toZ) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition.setNode(node);
        transition.setToX(toX);
        transition.setToY(toY);
        transition.setToZ(toZ);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.play();
    }

    public void setPlaneModelColor(Color color) {
        for (MeshView meshView : meshViews) {
            meshView.setMaterial(new PhongMaterial(color));
        }
        label.setFill(color);
    }
}
