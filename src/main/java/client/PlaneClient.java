package client;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;

@Log4j2
public class PlaneClient extends Client  {
    private Plane plane;
    private boolean isProcessCompleted;

    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane();
    }

    private void startCommunication() {
        try {
            startConnection();
            out.writeObject(plane);

            while (!isProcessCompleted) {
                plane.holdPattern();

                if (plane.isOutOfFuel()) {
                    log.info("Plane [{}] is out of fuel", plane.getId());
                    break;
                }

                out.reset();
                out.writeObject(plane.getLocation());
                out.flush();

                String instruction = (String) in.readObject();
                processAirportInstruction(instruction);
                Thread.sleep(1000);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to handle communication with plane [{}]: {}", plane.getId(), ex.getMessage());
        }
        log.info("Plane [{}] exited communication", plane.getId());
    }

    private void processAirportInstruction(String instruction) throws IOException, ClassNotFoundException {
        switch (instruction) {
            case "WAIT":
                log.info("Plane [{}] is waiting for an available runway", plane.getId());
                break;
            case "LAND":
                log.info("Runway available. Plane [{}] is preparing for landing", plane.getId());
                processLanding();
                isProcessCompleted = true;
                break;
            case "FULL":
                log.info("Plane [{}] is waiting for an available runway", plane.getId());
                isProcessCompleted = true;
                break;
        }
    }

    private void processLanding() throws IOException, ClassNotFoundException {
        Runway assignedRunway = (Runway) in.readObject();
        while (!plane.isLanded()) {
            out.writeObject(plane.getLocation());
            plane.proceedToLand(assignedRunway);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Landing process interrupted for plane [{}]", plane.getId());
            }
        }
        log.info("Plane [{}] has successfully landed on runway [{}]", plane.getId(), assignedRunway.getId());
    }

    public static void main(String[] args) throws IOException {
        PlaneClient client = new PlaneClient("localhost", 5000);
        client.startCommunication();
    }
}
