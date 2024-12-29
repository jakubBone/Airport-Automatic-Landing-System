package airport;

import location.Location;

public class RunwayFactory {
    public static Runway create(String runwayId, String corridorId, Location landingPoint, Location entryPoint, Location finalApproach) {
        Corridor corridor = new Corridor(corridorId, entryPoint, finalApproach);
        return new Runway(runwayId, landingPoint, corridor);
    }
}
