package animation.utills;

import animation.model.PlaneModel;
import controller.AirTrafficController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.util.Duration;

import plane.Plane;
import java.util.List;
import java.util.Map;

public class SceneUpdater {
    private final Group root;
    private AirTrafficController controller;
    private Map<Integer, PlaneModel> planeMap;

    public SceneUpdater(Group root, AirTrafficController controller, Map<Integer, PlaneModel> planeMap) {
        this.root = root;
        this.controller = controller;
        this.planeMap = planeMap;
    }

    public void start() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> updateAirspace()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateAirspace() {
        List<Plane> activePlanes = controller.getPlanes();

        for (Plane plane : activePlanes) {
            PlaneModel planeModel = planeMap.get(plane.getId());

            if (planeModel == null) {
                planeModel = new PlaneModel(plane);
                planeMap.put(plane.getId(), planeModel);
                root.getChildren().addAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            }

            if (plane.isDestroyed()) {
                root.getChildren().removeAll(planeModel.getPlaneSphere(), planeModel.getLabel());
            } else {
                planeModel.updatePosition(plane);
            }

            if (plane.isLanded()) {
                PlaneModel finalPlaneModel = planeModel;
                new Timeline(new KeyFrame(Duration.millis(1000), ev -> {
                    root.getChildren().removeAll(finalPlaneModel.getPlaneSphere(), finalPlaneModel.getLabel());
                    planeMap.remove(plane.getId());
                })).play();
            }
        }
    }
}
