package handler;

import airport.AirSpace;
import airport.AirTrafficController;
import airport.Runway;

import lombok.extern.log4j.Log4j2;
import location.Location;
import plane.Plane;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
public class PlaneHandler extends Thread {
    private Socket clientSocket;
    private AirTrafficController controller;
    private Lock lock;

    public PlaneHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.controller = new AirTrafficController();
        this.lock = new ReentrantLock();;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            Plane incomingPlane = (Plane) in.readObject();

            if (!isPlaneRegistrationSuccessful(incomingPlane, out)) {
                return;
            }

            while (true) {
                Location location = aquireCurrentLocation(in, incomingPlane);
                if(location == null){
                    controller.removePlaneFromSpace(incomingPlane);
                    return;
                }

                incomingPlane.setLocation(location);

                if(controller.isAnyRunwayAvailable()) {
                    Runway runway = controller.getAvailableRunway();
                    executeLandingProcedure(incomingPlane, runway, in, out);
                    break;
                } else {
                    log.info("Plane [{}] is waiting for empty runway", incomingPlane.getId());
                    out.writeObject("WAIT");
                }


                /*if(controller.isAnyRunwayAvailable()) {
                    Runway runway = controller.getAvailableRunway();
                    executeLandingProcedure(incomingPlane, runway, in, out);
                    break;
                } else {
                    log.info("Plane [{}] is waiting for empty runway", incomingPlane.getId());
                    out.writeObject("WAIT");
                }*/

            }

        } catch (IOException | ClassNotFoundException ex){
            log.error("Error occurred while handling client request:" + ex.getMessage());
        }
    }

    public void executeLandingProcedure(Plane plane, Runway runway, ObjectInputStream in, ObjectOutputStream out) throws IOException{
        log.info("Plane [{}] got approval for landing", plane.getId());
        out.writeObject("LAND");

        log.info("Plane [{}] assigned to runway [{}]", plane.getId(), runway.getId());
        out.writeObject(runway);

        monitorLandingPosition(in, plane);
        log.info("Plane [{}] has landed on  runway [{}]", plane.getId(), runway.getId());
        completeLandingProcedure(plane, runway);
    }


    public void monitorLandingPosition(ObjectInputStream in, Plane incomingPlane){
        while(true){
            Location location = aquireCurrentLocation(in, incomingPlane);

            if(location == null){
                if(incomingPlane.isLanded()){
                    log.info("Plane [{}] removed from radar after landing", incomingPlane.getId());
                } else {
                    log.error("Plane [{}] disappeared from the radar", incomingPlane.getId());
                }
                return;
            }
            incomingPlane.setLocation(location);
        }
    }

    public Location aquireCurrentLocation(ObjectInputStream in, Plane incomingPlane){
        try {
            return (Location) in.readObject();
        } catch (Exception ex) {
            log.error("Error reading location for Plane [{}]: {}", incomingPlane.getId(), ex.getMessage());
            return null;
        }
    }

    private boolean isPlaneRegistrationSuccessful(Plane incomingPlane, ObjectOutputStream out) throws IOException {
        if (controller.isSpaceFull()) {
            log.info("No capacity in the airspace");
            out.writeObject("FULL");
            return false;
        }

        controller.registerPlane(incomingPlane);
        log.info("Plane [{}] entered into airspace", incomingPlane.getId());
        return true;
    }

    public void completeLandingProcedure(Plane incomingPlane, Runway assignedRunway) throws IOException {
        controller.removePlaneFromSpace(incomingPlane);
        log.info("Plane [{}] removed from the airspace", incomingPlane.getId());

        controller.releaseRunway(assignedRunway);
        log.info("Runway [{}] released", assignedRunway.getId());
    }

}
