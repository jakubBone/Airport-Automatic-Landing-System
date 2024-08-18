package client;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;

@Log4j2
public class PlaneClient extends Client  {

    private Plane plane;
    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane();
    }

    private void startCommunication() {
        try {
            startConnection();
            out.writeObject(plane);

            while(true){
                plane.holdPattern();
                System.out.println("Current waypoint: " + plane.getCurrentWaypoint());

                if(plane.isOutOfFuel()){
                    log.info("Plane [{}] is out of fuel", plane.getId());
                    break;
                }

                out.reset();
                out.writeObject(plane.getLocation());
                out.flush();

                String instruction = (String) in.readObject();
                if ("WAIT".equals(instruction)) {
                    log.info("Plane [{}}] is waiting for a available runway", plane.getId());
                } else if ("LAND".equals(instruction)) {
                    Runway assignedRunway = (Runway) in.readObject();
                    log.info("Runway available. Plane [{}] is preparing for landing", plane.getId());
                    processLanding(assignedRunway);
                    break;
                } else if("FULL".equals(instruction)){
                    log.info("No capacity in the airspace for plane [{}]. Find another airport", plane.getId());
                    return;
                }

                Thread.sleep(1000);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to handle communication with plane [{}]: {}", plane.getId(), ex.getMessage());
        }
        log.info("Plane [{}] exited communication", plane.getId());
    }

    public void processLanding(Runway runway){
        log.info("Plane [{}}] is heading towards the runway", plane.getId());
        //plane.directLanding(runway);
    }

    public static void main(String[] args) throws IOException {
        PlaneClient client = new PlaneClient("localhost", 5000);
        client.startCommunication();
    }
}
