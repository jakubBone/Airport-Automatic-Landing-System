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
    private List<Location> descentWaypoints;

    private List<Sphere> landingR1Models;
    private List<Sphere> landingR2Models;
    private List<Sphere> holdingPatternModels;
    private List<Sphere> descentModels;

    public WaypointModel() {
        createWaypoint();
        createModels();
    }

    public void createWaypoint(){
        this.landingWaypointsR1 = WaypointGenerator.getLandingWaypoints(Airport.runway1.getCorridor().getEntryWaypoint());
        this.landingWaypointsR2 = WaypointGenerator.getLandingWaypoints(Airport.runway2.getCorridor().getEntryWaypoint());
        this.holdingPatternWaypoints = WaypointGenerator.getHoldingPatternWaypoints();
        this.descentWaypoints = WaypointGenerator.getDescentWaypoints();
    }

    private void createModels(){
        this.landingR1Models = new ArrayList<>();
        this.landingR2Models = new ArrayList<>();
        this.holdingPatternModels = new ArrayList<>();
        this.descentModels = new ArrayList<>();

        addModels(landingWaypointsR1, landingR1Models);
        addModels(landingWaypointsR2, landingR2Models);
        addModels(holdingPatternWaypoints, holdingPatternModels);
        addModels(descentWaypoints, descentModels);
    }

    private void addModels(List<Location> waypoints, List<Sphere> models){
        for (Location waypoint : waypoints) {
            Sphere waypointModel = new Sphere(10);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.BLACK);
            waypointModel.setMaterial(material);

            waypointModel.setTranslateX((waypoint.getX()) / 2);
            waypointModel.setTranslateY(-(waypoint.getAltitude()) / 2);

            waypointModel.setTranslateZ((waypoint.getY()) / 2);
            models.add(waypointModel);
        }
    }
}
