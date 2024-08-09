package client;

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

    private void startCommunication()  {
        try {
            startConnection();

            out.writeObject(plane);

            while(true){
                //updatePlaneState();
                Plane updatedPlane = (Plane) in.readObject();
                processAirportInstruction(updatedPlane);

                if(plane.getFuelLevel() <= 0){
                    break;
                }

                Thread.sleep(1000);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to handle communication with the {}: {}", plane.getPlaneId(), ex.getMessage());
        }
    }

    public void updatePlaneState(){
        plane.updateLocation();
        plane.reduceFuel();
    }

    public void processAirportInstruction(Plane updatedPlane){
        // set the plane's state based on the airport's instructions
        plane.setCurrentLocation(updatedPlane.getCurrentLocation());
        plane.setHeading(updatedPlane.getHeading());
        plane.setSpeed(updatedPlane.getSpeed());
    }

    public static void main(String[] args) throws IOException {
        PlaneClient client = new PlaneClient("localhost", 5000);
        client.startCommunication();
    }
}
