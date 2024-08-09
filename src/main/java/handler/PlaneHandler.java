package handler;

import airport.AirSpace;
import airport.AirTrafficController;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

@Log4j2
public class PlaneHandler implements Runnable {
    private Socket socket;
    private Plane plane;
    private AirSpace airSpace;
    private AirTrafficController controller;

    public PlaneHandler(Socket socket) {
        this.socket = socket;
        controller = new AirTrafficController();
    }

    public void handleClient() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getOutputStream());
             ObjectInputStream out = new ObjectInputStream(socket.getInputStream())) {

            plane = (Plane) in.readObject();

            log.info("Check number of planes in the airspace");
            if (airSpace.isAirSpaceFull()) {
                return;
            }

            plane.spawnPlaneAtRandomLocation();
            log.info("Plane " + plane.getPlaneId() + "fly into airspace");

            while (true) {
                log.info("Add plane to queue");
                airSpace.registerPlaneInAirSpace(plane);

                log.info("Check runways availability");
                controller.assignRunway(plane);

                log.info("Remove the plane from queue");

                airSpace.removePlaneFromAirSpace(plane);

            }
        } catch (IOException ex){
            log.error("Error occurred while handling client request: {}", ex.getMessage());
        }
    }
}
