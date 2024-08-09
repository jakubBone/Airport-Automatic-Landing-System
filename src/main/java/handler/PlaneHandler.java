package handler;

import airport.AirSpace;
import airport.AirTrafficController;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;

@Log4j2
public class PlaneHandler  {
    private Socket socket;
    private Plane plane;
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
            plane = (Plane) in.readObject();

            log.info("Check number of planes in the airspace");
            if (airSpace.isAirSpaceFull()) {
                return;
            }

            plane.spawnPlaneAtRandomLocation();
            log.info("Plane " + plane.getPlaneId() + " entered into airspace");

            while (true) {;
                airSpace.registerPlaneInAirSpace(plane);

                log.info("Check runways availability");
                controller.assignRunway(plane);

                log.info("Landing...");
                log.info("Landing...");
                log.info("Landing...");

                log.info("Remove the plane from queue");
                airSpace.removePlaneFromAirSpace(plane);

                // Release runway
                break;
            }
        } catch (IOException | ClassNotFoundException ex){
            log.error("Error occurred while handling {} request: {}", plane.getPlaneId(), ex.getMessage());
        }
    }
}
