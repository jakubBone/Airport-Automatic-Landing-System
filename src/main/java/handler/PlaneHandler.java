package handler;

import airport.AirSpace;
import airport.AirTrafficController;
import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Location;
import plane.Plane;

import java.io.*;
import java.net.Socket;

@Log4j2
public class PlaneHandler  {
    private Socket socket;
    private AirSpace airSpace;
    private AirTrafficController controller;

    public PlaneHandler(Socket socket) {
        this.socket = socket;
        this.airSpace = new AirSpace();
        this.controller = new AirTrafficController();
    }

    public void handleClient() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Plane incomingPlane = (Plane) in.readObject();
            if (airSpace.isAirSpaceFull()) {
                log.info("No capacity in the airspace");
                return;
            }
            airSpace.registerPlaneInAirSpace(incomingPlane);
            log.info("Plane [" + incomingPlane.getId() + "] entered into airspace");

            while (true) {
                Location currentLocation = (Location) in.readObject();
                incomingPlane.setLocation(currentLocation);

                log.info("Check runways availability");
                if(controller.isAnyRunwayAvailable()){
                    log.info("Plane [" + incomingPlane.getId() + "] got approval for landing");
                    out.writeObject("LAND");

                    Runway runway = controller.assignRunway();
                    log.info("Plane [" + incomingPlane.getId() + "] assigned to runway [" + runway.getId());

                    out.writeObject(runway);

                    log.info("Landing...");

                    airSpace.removePlaneFromAirSpace(incomingPlane);
                    log.info("Plane [" + incomingPlane.getId() + "] removed from the airspace");

                    controller.releaseRunway(runway);
                    log.info("Runway [" + runway.getId() + "] removed from the airspace");
                    break;
                } else {
                    log.info("Plane [" + incomingPlane.getId() + "] is waiting for empty runway");
                    out.writeObject("WAIT");
                }

                //airSpace.updateAirspace();
            }
        } catch (IOException | ClassNotFoundException ex){
            log.error("Error occurred while handling client request:" + ex.getMessage());
        }
    }
}
