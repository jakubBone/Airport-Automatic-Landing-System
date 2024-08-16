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
                Location location = aquireCurrentLocation(in, incomingPlane);
                if(location == null){
                    airSpace.removePlaneFromSpace(incomingPlane);
                    return;
                }
                incomingPlane.setLocation(location);

                if(controller.isAnyRunwayAvailable()) {
                    log.info("Plane [{}] got approval for landing", incomingPlane.getId());
                    out.writeObject("LAND");

                    Runway runway = controller.getAvailableRunway();
                    log.info("Plane [{}] assigned to runway [{}]", incomingPlane.getId(), runway.getId());
                    out.writeObject(runway);

                    postLandingClearance(incomingPlane, runway);
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

    public Location aquireCurrentLocation(ObjectInputStream in, Plane incomingPlane){
        Location currentLocation = null;
        try {
            currentLocation = (Location) in.readObject();
        } catch (Exception ex) {
            log.error("Plane [{}] disappeared from the radar. Error: {}", incomingPlane.getId(), ex.getMessage());
        }
        return currentLocation;
    }

    public void postLandingClearance(Plane incomingPlane, Runway assignedRunway) throws IOException {
        airSpace.removePlaneFromSpace(incomingPlane);
        log.info("Plane [{}] removed from the airspace", incomingPlane.getId());

        controller.releaseRunway(assignedRunway);
        log.info("Runway [{}] released", assignedRunway.getId());
    }


    /*public void handleClient() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Plane incomingPlane = (Plane) in.readObject();

            if (!isPlaneRegistrationSuccessful(incomingPlane, out)) {
                return;
            }

            Runway assignedRunway = waitForRunwayAssignment(in, out, incomingPlane);
            if (assignedRunway != null) {
                postLandingClearance(incomingPlane, assignedRunway);
            }

        } catch (IOException | ClassNotFoundException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage());
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

    private Runway waitForRunwayAssignment(ObjectInputStream in, ObjectOutputStream out, Plane incomingPlane) throws IOException, ClassNotFoundException {
        while (true) {
            Location location = acquireCurrentLocation(in, incomingPlane);
            if (location == null) {
                airSpace.removePlane(incomingPlane);
                return null;
            }
            incomingPlane.setLocation(location);

            if (controller.isAnyRunwayAvailable()) {
                log.info("Plane [{}] got approval for landing", incomingPlane.getId());
                out.writeObject("LAND");

                Runway runway = controller.getAvailableRunway();
                log.info("Plane [{}] assigned to runway [{}]", incomingPlane.getId(), runway.getId());
                out.writeObject(runway);

                return runway;
            } else {
                log.info("Plane [{}] is waiting for an empty runway", incomingPlane.getId());
                out.writeObject("WAIT");
            }
        }
    }

    private Location acquireCurrentLocation(ObjectInputStream in, Plane incomingPlane) {
        try {
            return (Location) in.readObject();
        } catch (Exception ex) {
            log.error("Plane [{}] disappeared from the radar. Error: {}", incomingPlane.getId(), ex.getMessage());
            return null;
        }
    }

    private void postLandingClearance(Plane incomingPlane, Runway assignedRunway) {
        airSpace.removePlane(incomingPlane);
        log.info("Plane [{}] removed from the airspace", incomingPlane.getId());

        controller.releaseRunway(assignedRunway);
        log.info("Runway [{}] released", assignedRunway.getId());
    }
}*/

}
