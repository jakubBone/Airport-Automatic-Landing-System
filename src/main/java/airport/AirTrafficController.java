package airport;

import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.util.LinkedList;
import java.util.Queue;

@Log4j2
public class AirTrafficController {
    public static Queue <Runway> availableRunways = new LinkedList<>();
    public AirTrafficController() {
        initRunways();
    }
    public void initRunways(){
        Runway runway1 = new Runway("1");
        Runway runway2 = new Runway("2");
        availableRunways.add(runway1);
        availableRunways.add(runway2);
    }

    public void assignRunway(Plane plane){
        if (availableRunways.isEmpty()) {
            log.info("No empty runways available");
            waitForRunway(plane);
        }

        log.info("Assigning the runway");
        Runway assignedRunway = availableRunways.poll();
        log.info("Plane " + plane.getPlaneId() + " assigned to runway " + assignedRunway.getId());

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