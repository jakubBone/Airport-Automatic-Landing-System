package com.jakub.bone.utills;

import com.jakub.bone.domain.plane.Plane;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlaneMapper {
    public static Map<String, Object> mapPlane(Plane plane){
        Map<String, Object> planeMap = new LinkedHashMap<>();
        planeMap.put("flightNumber", plane.getFlightNumber());
        planeMap.put("phase", plane.getPhase());

        Map<String, Object> locationMap = new LinkedHashMap<>();
        locationMap.put("x", plane.getNavigator().getLocation().getX());
        locationMap.put("y", plane.getNavigator().getLocation().getY());
        locationMap.put("altitude", plane.getNavigator().getLocation().getAltitude());

        planeMap.put("location", locationMap);
        planeMap.put("fuel level", plane.getFuelManager().getFuelLevel());

        return planeMap;
    }
}
