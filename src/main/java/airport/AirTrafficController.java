package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

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
        //Runway runway1 = new Runway("1", new Location(1, 0, 0));
        //Runway runway2 = new Runway("2", new Location(2, 0 , 0));
        //availableRunways.add(runway1);
        //availableRunways.add(runway2);
    }

    public Runway assignRunway() {
        return availableRunways.poll();
    }

    public boolean isAnyRunwayAvailable(){
        return !availableRunways.isEmpty();
    }

    public void releaseRunway(Runway runway) {
        availableRunways.add(runway);
    }
}