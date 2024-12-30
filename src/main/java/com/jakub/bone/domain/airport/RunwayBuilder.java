package com.jakub.bone.domain.airport;

public class RunwayBuilder {
    public static Runway create(String runwayId, String corridorId, Location landingPoint, Location entryPoint, Location finalApproach) {
        Corridor corridor = new Corridor(corridorId, entryPoint, finalApproach);
        return new Runway(runwayId, landingPoint, corridor);
    }
}
