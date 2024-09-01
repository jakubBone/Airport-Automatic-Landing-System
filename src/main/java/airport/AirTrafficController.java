package airport;

import location.WaypointGenerator;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;

import java.util.Queue;

@Log4j2
@Getter
public class AirTrafficController {
    public Queue <Runway> availableRunways = new LinkedList<>();

    public AirTrafficController() {
        initRunways();
    }

    public void initRunways(){
        createRunwayWithCorridor("R-1", "C-1", new WaypointGenerator(1000, 2000), new WaypointGenerator(-5000, 2000));
        createRunwayWithCorridor("R-2", "C-2", new WaypointGenerator(1000, -2000), new WaypointGenerator(-5000, -2000));
    }
    public void createRunwayWithCorridor(String runwayId, String corridorId, WaypointGenerator runwayWaypoint, WaypointGenerator corrindorWaypoint){
        Corridor corridor = new Corridor(corridorId, corrindorWaypoint);
        Runway runway = new Runway(runwayId, runwayWaypoint, corridor);
        availableRunways.add(runway);
    }

    public Runway getAvailableRunway() {
        return availableRunways.poll();
    }

    public boolean isAnyRunwayAvailable(){
        return !availableRunways.isEmpty();
    }

    public void releaseRunway(Runway runway) {
        availableRunways.add(runway);
    }
}