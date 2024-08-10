package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import plane.Location;
import plane.Plane;

import java.util.LinkedList;
import java.util.Queue;

@Log4j2
@Getter
public class AirTrafficController {
    public static Queue <Runway> availableRunways = new LinkedList<>();
    public AirTrafficController() {
        initRunways();
    }
    public void initRunways(){
        Runway runway1 = new Runway("1", new Location(1, 0, 0));
        Runway runway2 = new Runway("2", new Location(2, 0 , 0));
        availableRunways.add(runway1);
        availableRunways.add(runway2);
    }

    public Runway assignRunway(Plane plane){
        if (availableRunways.isEmpty()) {
            log.info("No empty runways available");
            waitForRunway(plane);
        }

        log.info("Assigning the runway");
        Runway assignedRunway = availableRunways.poll();
        log.info("Plane " + plane.getPlaneId() + " assigned to runway " + assignedRunway.getId());

        return assignedRunway;
    }

    public void waitForRunway(Plane plane) {
        log.info("Plane " + plane.getPlaneId() + "is waiting for empty runway");
        while(availableRunways.isEmpty()){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ex){
                log.error("Error occurred while waiting for landing: {}", ex.getMessage());
            }
        }
    }

    public void releaseRunway(Runway runway) {
        availableRunways.add(runway);
    }

    public void directPlane(){

    }

    public void checkCollision(){

    }
}