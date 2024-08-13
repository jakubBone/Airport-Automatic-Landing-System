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
                plane.reduceFuel();

                if(plane.isOutOfFuel()){
                    log.info("Plane [" + plane.getId() + "] is out of fuel");
                    break;
                }

                out.reset();
                out.writeObject(plane.getLocation());
                out.flush();

                String instruction = (String) in.readObject();
                if ("WAIT".equals(instruction)) {
                    log.info("Plane [" + plane.getId() + "] is waiting for a available runway");
                } else if ("LAND".equals(instruction)) {
                    Runway assignedRunway = (Runway) in.readObject();
                    log.info("Runway available. Plane [" + plane.getId() + "] is preparing for landing");
                    processLanding(assignedRunway);
                    break;
                } else if("FULL".equals(instruction)){
                    log.info("No capacity in the airspace. Find another airport");
                    return;
                }

                Thread.sleep(1000);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to handle communication with plane [{}]: {}", plane.getId(), ex.getMessage());
        }
        log.info("Plane [" + plane.getId() + "] exited communication");
    }

    public void processLanding(Runway runway){
        log.info("Plane [" + plane.getId() + "] is heading towards the runway");
        plane.directLanding(runway);
    }

    public static void main(String[] args) throws IOException {
        PlaneClient client = new PlaneClient("localhost", 5000);
        client.startCommunication();
    }
}
