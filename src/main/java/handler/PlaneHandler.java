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
            if (airSpace.isSpaceFull()) {
                log.info("No capacity in the airspace");
                out.writeObject("FULL");
                return;
            }

            airSpace.registerPlane(incomingPlane);
            log.info("Plane [{}] entered into airspace", incomingPlane.getId());

            while (true) {
                if (!isLocationUpdated(in, incomingPlane)) {
                    airSpace.removePlane(incomingPlane);
                    return;
                }

                log.info("Check runways availability");
                if(controller.isAnyRunwayAvailable()){
                    assignRunwayAndLand(out, incomingPlane);
                    break;
                } else {
                    log.info("Plane [{}] is waiting for empty runway", incomingPlane.getId());
                    out.writeObject("WAIT");
                }
            }
        } catch (IOException | ClassNotFoundException ex){
            log.error("Error occurred while handling client request:" + ex.getMessage());
        }
    }

    public boolean isLocationUpdated(ObjectInputStream in, Plane incomingPlane){
        Location currentLocation = null;
        try {
            currentLocation = (Location) in.readObject();
            incomingPlane.setLocation(currentLocation);
            return true;
        } catch (Exception ex) {
            log.error("Plane [{}] disappeared from the radar. Error: {}", incomingPlane.getId(), ex.getMessage());
            airSpace.removePlane(incomingPlane);
            return false;
        }
    }

    public void assignRunwayAndLand(ObjectOutputStream out, Plane incomingPlane) throws IOException {
        log.info("Plane [{}] got approval for landing", incomingPlane.getId());
        out.writeObject("LAND");

        Runway runway = controller.assignRunway();
        log.info("Plane [{}] assigned to runway [{}]", incomingPlane.getId(), runway.getId());

        out.writeObject(runway);
        log.info("Landing...");

        airSpace.removePlane(incomingPlane);
        log.info("Plane [{}] removed from the airspace", incomingPlane.getId());

        controller.releaseRunway(runway);
        log.info("Runway [{}] released", runway.getId());
    }
}
