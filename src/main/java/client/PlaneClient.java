package client;

import airport.Runway;
import communication.Messenger;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import plane.Plane;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static handler.PlaneHandler.AirportInstruction;

@Log4j2
public class PlaneClient extends Client implements Runnable {
    private Plane plane;
    private boolean isProcessCompleted;
    private Messenger messenger;


    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane();
        this.messenger = new Messenger();
        log.info("PlaneClient created for Plane [{}] at IP: {}, Port: {}", plane.getId(), ip, port);
    }

    @Override
    public void run() {
        try {
            startConnection();
            if(!isConnected){
                return;
            }

            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                log.warn("PlaneClient [{}]: Sleep was interrupted", plane.getId());
                Thread.currentThread().interrupt();
            }

            messenger.send(plane, out);

            while (!isProcessCompleted) {
                if (plane.isOutOfFuel()) {
                    log.info("Plane [{}] is out of fuel. Collision", plane.getId());
                    messenger.send("OUT_OF_FUEL", out);
                    break;
                }

                if(plane.getLocation() != null){
                    messenger.send(plane.getLocation(), out);
                } else {
                    log.info("Plane [{}] disappeared from the radar", plane.getId());
                    break;
                }
                String message = messenger.receive(in);
                AirportInstruction instruction = messenger.parse(message, AirportInstruction.class);
                processInstruction(instruction);
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}]: Communication failure: {}", plane.getId(), ex.getMessage());
        } finally {
            log.info("Plane [{}] exited communication", plane.getId());
            stopConnection();
        }
    }



    private void processInstruction(AirportInstruction instruction) throws IOException, ClassNotFoundException{
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

    private void processLanding() throws IOException, ClassNotFoundException {
        String message = messenger.receive(in);
        Runway runway = messenger.parse(message, Runway.class);

        plane.setLandingPhase(runway);
        log.info("Plane [{}] assigned to LAND on runway {{}]", plane.getId(), runway.getId());

        while (!plane.isLanded()) {
            if (plane.isOutOfFuel()) {
                log.info("Plane [{}] is out of fuel. Collision", plane.getId());
                messenger.send("OUT_OF_FUEL", out);
                return;
            }

            plane.land(runway);

            messenger.send(plane.getLocation(), out);

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
