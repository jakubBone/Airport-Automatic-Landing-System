package airport;

import location.Location;
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
        createRunwayWithCorridor("R-1", new Location(-5000, 2000, 2000),new Location(1000, 2000, 0), new Location(1000, 2000, 0), new Location(5000, 2000, 0));
        createRunwayWithCorridor("R-2", new Location(-5000, -2000, 2000),new Location(1000, -2000, 0), new Location(1000, -2000, 0), new Location(5000, -2000, 0));
    }
    public void createRunwayWithCorridor(String id, Location startCorridor, Location endCorridor, Location startRunway, Location endRunway){
        Corridor corridor = new Corridor(startCorridor, endCorridor);
        Runway runway = new Runway(id, startRunway, endRunway, corridor);
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