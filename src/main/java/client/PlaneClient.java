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
            case DESCENT -> executeDescent();
            case HOLD_PATTERN -> executeHoldPattern();
            case LAND -> executeLanding();
            case FULL -> executeFullAirspace();
            case OCCUPIED -> executeOccupiedLocation();
            case COLLISION -> executeCollision();
            default -> log.warn("Unknown instruction for Plane [{}]: [{}]", plane.getId(), instruction);
        }
    }

    private void executeDescent() {
        log.info("Plane [{}] instructed to DESCENT", plane.getId());
        plane.descend();
    }

    private void executeHoldPattern() {
        log.info("Plane [{}] instructed to HOLD_PATTERN", plane.getId());
        plane.hold();
    }

    private void executeLanding() throws IOException, ClassNotFoundException {
        processLanding();
        isProcessCompleted = true;
    }

    private void executeFullAirspace() {
        log.info("Airspace is FULL. Plane [{}] instructed to find an alternative airport. Stopping communication", plane.getId());
        isProcessCompleted = true;
    }

    private void executeOccupiedLocation() {
        log.info("Initial location OCCUPIED. Plane [{}] cannot be registered in the location. Stopping communication", plane.getId());
        isProcessCompleted = true;
    }

    private void executeCollision() {
        log.info("COLLISION detected for Plane [{}]. Stopping communication", plane.getId());
        disableReconnection();
        isProcessCompleted = true;
    }

    private void processLanding() throws IOException, ClassNotFoundException {
        Runway runway = (Runway) in.readObject();
        plane.setLandingPhase(runway);
        log.info("Plane [{}] assigned to LAND on runway {{}]", plane.getId(), runway.getId());

        while (!plane.isLanded()) {
            if (plane.isOutOfFuel()) {
                log.info("Plane [{}] is out of fuel. Collision", plane.getId());
                sendMessage("OUT_OF_FUEL");
                return;
            }

            plane.land(runway);

            sendMessage(plane.getLocation());

            if (plane.isLanded()) {
                log.info("Plane [{}] has successfully landed on runway {{}]", plane.getId(), runway.getId());
                return;
            }

            if(plane.getLocation().getAltitude() < 0){
                log.info("RUNWAY COLLISION detected for Plane [{}]", plane.getId());
                return;
            }
        }
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

           sendMessage(plane);

            while (!isProcessCompleted) {
                if (plane.isOutOfFuel()) {
                    log.info("Plane [{}] is out of fuel. Collision", plane.getId());
                    sendMessage("OUT_OF_FUEL");
                    break;
                }

                if(plane.getLocation() != null){
                    sendMessage(plane.getLocation());
                } else {
                    log.error("Plane [{}] disappeared from the radar", plane.getId());
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



    private void sendMessage(Object message) throws IOException {
        out.reset();
        out.writeObject(message);
        out.flush();
    }

    public static void main(String[] args) throws IOException {
        int numberOfClients = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            executorService.execute(client);
        }
        executorService.shutdown();
    }
}
