package com.jakub.bone.domain.airport;

import com.jakub.bone.domain.airport.Corridor;
import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.domain.airport.Runway;

public class RunwayFactory {
    public static Runway create(String runwayId, String corridorId, Location landingPoint, Location entryPoint, Location finalApproach) {
        Corridor corridor = new Corridor(corridorId, entryPoint, finalApproach);
        return new Runway(runwayId, landingPoint, corridor);
    }
}
