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

            log.info("Check number of planes in the airspace");
            if (airSpace.isAirSpaceFull()) {
                log.info("Airspace if full");
                return;
            }

            airSpace.registerPlaneInAirSpace(incomingPlane);
            log.info("Plane [" + incomingPlane.getPlaneId() + "] entered into airspace");

            while (true) {;
                log.info("Check runways availability");
                Runway runway = controller.assignRunway(incomingPlane);

                out.writeObject(runway);

                log.info("Landing...");
                log.info("Landing...");
                log.info("Landing...");

                log.info("Remove the plane from queue");
                // airSpace.removePlaneFromAirSpace(incomingPlane);

                controller.releaseRunway(runway);
                break;
            }
        } catch (IOException | ClassNotFoundException ex){
            log.error("Error occurred while handling {} request: {}", requestedPlane.getPlaneId(), ex.getMessage());
        }
    }
}
