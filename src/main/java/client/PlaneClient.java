package client;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Location;
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
                updatePlaneState();

                if(plane.getFuelLevel() <= 0){
                    break;
                }

                Runway runway = (Location) in.readObject();
                processAirportInstruction(plane, runway);

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

    public void processAirportInstruction(Plane plane, Runway runway){
        // landing handling
    }



    public static void main(String[] args) throws IOException {
        PlaneClient client = new PlaneClient("localhost", 5000);
        client.startCommunication();
    }
}
