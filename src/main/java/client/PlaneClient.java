package client;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;

import static handler.PlaneHandler.AirportInstruction;

@Log4j2
public class PlaneClient extends Client  {
    private Plane plane;
    private boolean isProcessCompleted;

    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane();
        log.info("PlaneClient created for Plane [{}] at IP: {}, Port: {}", plane.getId(), ip, port);
    }

    private void startCommunication() {
        try {
            startConnection();

            out.writeObject(plane);

            while (!isProcessCompleted) {

                if (plane.isOutOfFuel()) {
                    log.info("Plane [{}] is out of fuel, exiting communication loop", plane.getId());
                    break;
                }

                if(plane.getLocation() != null){
                    out.reset();
                    out.writeObject(plane.getLocation());
                    out.flush();
                } else {
                    log.error("Plane [{}] disappeared from the radar ", plane.getId());
                    break;
                }

                AirportInstruction instruction = (AirportInstruction) in.readObject();
                processAirportInstruction(instruction);
                Thread.sleep(1000);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to handle communication with plane [{}]: {}", plane.getId(), ex.getMessage());
        } finally {
            log.info("Plane [{}] exited communication", plane.getId());
            stopConnection();
        }
    }

    private void processAirportInstruction(AirportInstruction instruction) throws IOException, ClassNotFoundException{
        switch (instruction) {
            case DESCENT:
                log.info("Plane [{}] instructed to DESCENT", plane.getId());
                plane.descend();
                break;
            case HOLD_PATTERN:
                log.info("Plane [{}] instructed to HOLD_PATTERN", plane.getId());
                plane.hold();
                break;
            case LAND:
                log.info("Plane [{}] instructed to LAND", plane.getId());
                processLanding();
                break;
            case FULL:
                log.info("Airspace is FULL. Plane [{}] cannot land. Searching for alternative airport", plane.getId());
                isProcessCompleted = true;
                break;
            case COLLISION:
                log.info("COLLISION detected for Plane [{}]. Stopping communication", plane.getId());
                disableReconnection();
                isProcessCompleted = true;
                break;
            default:
                log.warn("Unknown instruction for Plane [{}]: [{}]", plane.getId(), instruction);
                break;
        }
    }

    private void processLanding() throws IOException, ClassNotFoundException {
        Runway runway = (Runway) in.readObject();
        plane.setLandingPhase(runway);
        log.info("Plane [{}] assigned to LAND on runway {{}]", plane.getId(), runway.getId());

        while (!plane.isLanded()) {
            plane.land(runway);
            out.writeObject(plane.getLocation());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Landing process interrupted for Plane [{}]", plane.getId());
            }

            if (plane.isLanded()) {
                log.info("Plane [{}] has successfully landed", plane.getId());
                out.writeObject("LANDED");
                isProcessCompleted = true;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 80; i++) {
            new Thread(() -> {
                PlaneClient client = new PlaneClient("localhost", 5000);
                client.startCommunication();
                client.stopConnection();
            }).start();
        }
    }
}
