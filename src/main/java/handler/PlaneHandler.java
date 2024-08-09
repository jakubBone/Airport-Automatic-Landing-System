package handler;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
@Log4j2
public class PlaneHandler implements Runnable {
    private Socket socket;
    private static final int MAX_PLANES = 100;
    private Plane plane;
    private static ArrayList<Plane> planes;
    private static Queue <Runway> availableRunways;

    public PlaneHandler(Socket socket) {
        this.socket = socket;
        this.planes = new ArrayList<>();
        this.availableRunways = new LinkedList<>();
    }

    public void handleClient() {

        try (ObjectInputStream in = new ObjectInputStream(socket.getOutputStream());
             ObjectInputStream out = new ObjectInputStream(socket.getInputStream())) {

            plane = (Plane) in.readObject();

            log.info("Check number of planes in the queue");
            if (planes.size() >= MAX_PLANES) {
                return;
            }

            plane.spawnPlaneAtRandomLocation();
            log.info("Plane " + plane.getPlaneId() + " connected");

            while (true) {

                log.info("Add plane to queue");
                planes.add(plane);

                log.info("Check the runways availability");
                if (availableRunways.isEmpty()) {
                    log.info("No empty runways available");
                    waitForRunway();
                }

                log.info("Assigning the runway");
                Runway assignedRunway = availableRunways.poll();
                log.info("Plane " + plane.getPlaneId() + " assigned to runway " + assignedRunway.getId());

                log.info("Landing...");
                assignedRunway.landPlane(plane);
                availableRunways.add(assignedRunway);

                log.info("Remove the plane from queue");
                planes.remove(plane);

            }
        } catch (IOException ex){
            log.error("Error occurred while handling client request: {}", ex.getMessage());
        }
    }

    public void waitForRunway() {
        log.info("Plane " + plane.getPlaneId() + "is waiting for empty runway");
        while(availableRunways.isEmpty()){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ex){
                log.error("Error occurred while waiting for landing: {}", ex.getMessage());
            }
        }
    }
}
