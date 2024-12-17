package animation.model;

import com.interactivemesh.jfx.importer.ModelImporter;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import location.Location;
import lombok.Getter;
import plane.Plane;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;


@Getter
public class PlaneModel {
    private Plane plane;
    private Group planeGroup;
    private MeshView[] meshViews;
    private Text label;
    private PhongMaterial material;


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
        //importer.read(getClass().getResource("/models/airbusA380/airbusModel.obj"));

        //material = new PhongMaterial(Color.BLACK);
        meshViews = importer.getImport();
        /*for(MeshView meshView: meshViews){
            meshView.setMaterial(material);
        }*/
        planeGroup.getChildren().addAll(meshViews);
    }

    public void createLabel() {
        this.label = new Text();
        this.label.setFont(new Font(100));
        this.label.setFill(Color.BLACK);
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
        double toPlaneSphereX = nextLocation.getX() / 2.0;
        double toPlaneSphereY = -nextLocation.getAltitude() / 2.0;
        double toPlaneSphereZ = nextLocation.getY() / 2.0;
        double toLabelX = (nextLocation.getX() + 150) / 2.0;
        double toLabelY =  - nextLocation.getAltitude() / 2.0;
        double toLabelZ = (nextLocation.getY() + 150) / 2.0;

        animatePlane(planeGroup, toPlaneSphereX, toPlaneSphereY, toPlaneSphereZ);
        animatePlane(label, toLabelX, toLabelY, toLabelZ);

    }

    public void animatePlane(Node node, double toX, double toY, double toZ) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition.setNode(node);
        transition.setToX(toX);
        transition.setToY(toY);
        transition.setToZ(toZ);

        transition.setInterpolator(Interpolator.LINEAR);

        transition.play();
    }
}
