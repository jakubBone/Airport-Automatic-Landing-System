package handler;

import airport.AirSpace;
import airport.AirTrafficController;
import airport.Runway;

import lombok.extern.log4j.Log4j2;
import location.Location;
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

            if (!isPlaneRegistrationSuccessful(incomingPlane, out)) {
                return;
            }

            while (true) {
                Location location = aquireCurrentLocation(in, incomingPlane);
                if(location == null){
                    airSpace.removePlaneFromSpace(incomingPlane);
                    return;
                }

                incomingPlane.setLocation(location);

                if(controller.isAnyRunwayAvailable()) {
                    startLandingProcedure(incomingPlane, in, out);
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

    public void startLandingProcedure(Plane plane, ObjectInputStream in, ObjectOutputStream out) throws IOException{
        log.info("Plane [{}] got approval for landing", plane.getId());
        out.writeObject("LAND");

        Runway runway = controller.getAvailableRunway();
        log.info("Plane [{}] assigned to runway [{}]", plane.getId(), runway.getId());
        out.writeObject(runway);

        updatePosition(in, plane);
        log.info("Plane [{}] has landed on  runway [{}]", plane.getId(), runway.getId());
        postLandingClearance(plane, runway);
    }


    public void updatePosition(ObjectInputStream in, Plane incomingPlane){
        while(!incomingPlane.hasLanded()){
            Location location = aquireCurrentLocation(in, incomingPlane);
            incomingPlane.setLocation(location);
            if(location == null){
                return;
            }
        }
    }
    public Location aquireCurrentLocation(ObjectInputStream in, Plane incomingPlane){
        try {
            return (Location) in.readObject();
        } catch (Exception ex) {
            log.error("Plane [{}] disappeared from the radar. Error: {}", incomingPlane.getId(), ex.getMessage());
            return null;
        }
    }

    private boolean isPlaneRegistrationSuccessful(Plane incomingPlane, ObjectOutputStream out) throws IOException {
        if (airSpace.isSpaceFull()) {
            log.info("No capacity in the airspace");
            out.writeObject("FULL");
            return false;
        }

        airSpace.registerPlane(incomingPlane);
        log.info("Plane [{}] entered into airspace", incomingPlane.getId());
        return true;
    }

    public void postLandingClearance(Plane incomingPlane, Runway assignedRunway) throws IOException {
        airSpace.removePlaneFromSpace(incomingPlane);
        log.info("Plane [{}] removed from the airspace", incomingPlane.getId());

        controller.releaseRunway(assignedRunway);
        log.info("Runway [{}] released", assignedRunway.getId());
    }
}
