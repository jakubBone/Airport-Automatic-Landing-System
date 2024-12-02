package animation.model;

import airport.Airport;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import location.Location;
import location.WaypointGenerator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WaypointModel {
    private List<Location> landingWaypointsR1;
    private List<Location> landingWaypointsR2;
    private List<Location> holdingPatternWaypoints;
    private List<Location> holdingAlternativePatternWaypoints;
    private List<Location> descentWaypoints;
    private List<Sphere> landingR1Models;
    private List<Sphere> landingR2Models;
    private List<Sphere> holdingPatternModels;
    private List<Sphere> holdingAlternativePatternModels;
    private List<Sphere> descentModels;

    public WaypointModel() {
        createWaypoint();
        createModels();
    }

    public void createWaypoint(){
        this.landingWaypointsR1 = WaypointGenerator.getLandingWaypoints(Airport.runway1);
        this.landingWaypointsR2 = WaypointGenerator.getLandingWaypoints(Airport.runway2);
        this.holdingPatternWaypoints = WaypointGenerator.getHoldingPatternWaypoints();
        this.holdingAlternativePatternWaypoints = WaypointGenerator.getAlternativeHoldingPatternWaypoints();
        this.descentWaypoints = WaypointGenerator.getDescentWaypoints();
    }

    private void createModels(){
        this.landingR1Models = new ArrayList<>();
        this.landingR2Models = new ArrayList<>();
        this.holdingPatternModels = new ArrayList<>();
        this.descentModels = new ArrayList<>();
        this.holdingAlternativePatternModels = new ArrayList<>();

        addModels(landingWaypointsR1, landingR1Models, getColorsForWaypoints(landingWaypointsR1));
        addModels(landingWaypointsR2, landingR2Models, getColorsForWaypoints(landingWaypointsR2));
        addModels(holdingPatternWaypoints, holdingPatternModels, getColorsForWaypoints(holdingPatternWaypoints));
        addModels(holdingAlternativePatternWaypoints, holdingAlternativePatternModels, getColorsForWaypoints(holdingAlternativePatternWaypoints));
        addModels(descentWaypoints, descentModels, getColorsForWaypoints(descentWaypoints));
    }

    private void addModels(List<Location> waypoints, List<Sphere> models, Color color){
        for (Location waypoint : waypoints) {
            Sphere waypointModel = new Sphere(10);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(color);
            waypointModel.setMaterial(material);

            waypointModel.setTranslateX((waypoint.getX()) / 2);
            waypointModel.setTranslateY(-(waypoint.getAltitude()) / 2);

            waypointModel.setTranslateZ((waypoint.getY()) / 2);
            models.add(waypointModel);
        }
    }

    private Color getColorsForWaypoints(List<Location> waypoints) {
        if (waypoints == landingWaypointsR1 || waypoints == landingWaypointsR2) return Color.YELLOW;
        if (waypoints == holdingPatternWaypoints) return Color.LIGHTBLUE;
        if (waypoints == holdingAlternativePatternWaypoints) return Color.RED;
        if (waypoints == descentWaypoints) return Color.BLACK;
        return Color.GRAY;
    }
}
