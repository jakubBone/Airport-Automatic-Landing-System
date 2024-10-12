package client;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static handler.PlaneHandler.AirportInstruction;

@Log4j2
public class PlaneClient extends Client implements Runnable {
    private Plane plane;
    private boolean isProcessCompleted;

    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane();
        log.info("PlaneClient created for Plane [{}] at IP: {}, Port: {}", plane.getId(), ip, port);
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
                log.info("Airspace is FULL. Plane [{}] instructed look for a alternative airport. Stopping communication", plane.getId());                isProcessCompleted = true;
                break;
            case OCCUPIED:
                log.info("Initial location OCCUPIED. Plane [{}] cannot be registered in the location. Stopping communication", plane.getId());
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
        }
        log.info("Plane [{}] has successfully landed", plane.getId());
        isProcessCompleted = true;
    }

    @Override
    public void run() {
        try {
            startConnection();

            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }

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
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.error("Failed to handle communication with plane [{}]: {}", plane.getId(), ex.getMessage());
        } finally {
            log.info("Plane [{}] exited communication", plane.getId());
            stopConnection();
        }
    }

    public static void main(String[] args) throws IOException {
        int numberOfClients = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            executorService.execute(client);
        }

        executorService.shutdown();
    }
}
