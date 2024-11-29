package client;

import airport.Runway;
import utills.Messenger;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static controller.PlaneHandler.AirportInstruction;

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

            sendInitialData();
            handleInstructions();

        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}]: Communication failure: {}", plane.getId(), ex.getMessage());
        } finally {
            log.info("Plane [{}] exited communication", plane.getId());
            stopConnection();
        }
    }

    private void sendInitialData() throws IOException {
        messenger.send(plane, out);
    }

    private void handleInstructions() throws IOException, ClassNotFoundException {
        while (!isProcessCompleted) {
            if(!sendFuelLevel() || !sendPlaneLocation()){
                return;
            }

            processInstruction();

            if(plane.isDestroyed()){
                return;
            }
        }
    }
    private boolean sendFuelLevel() throws IOException {
        messenger.send(plane.getFuelLevel(), out);
        out.flush();

        if (plane.isOutOfFuel()) {
            log.info("Plane [{}] is out of fuel. Collision", plane.getId());
            return false;
        }
        return true;
    }

    private boolean sendPlaneLocation() throws IOException {
        if(plane.getLocation() == null) {
            log.info("Plane [{}] disappeared from the radar", plane.getId());
            return false;
        }
        messenger.send(plane.getLocation(), out);
        out.flush();
        return true;
    }
    private void processInstruction() throws IOException, ClassNotFoundException {
        AirportInstruction instruction = messenger.receiveAndParse(in, AirportInstruction.class);
        switch (instruction) {
            case DESCENT -> executeDescent();
            case HOLD_PATTERN -> executeHoldPattern();
            case ALTERNATIVE -> executeAlternative();
            case LAND -> processLanding();
            case FULL -> executeFullAirspace();
            case OCCUPIED -> executeOccupiedLocation();
            case COLLISION -> executeCollision();
            default -> log.warn("Unknown instruction for Plane [{}]: [{}]", plane.getId(), instruction);
        }
    }

    private void processLanding() throws IOException, ClassNotFoundException {
        Runway runway = messenger.receiveAndParse(in, Runway.class);
        plane.setLandingPhase(runway);

        log.info("Plane [{}] assigned to LAND on runway {{}]", plane.getId(), runway.getId());
        while (!isProcessCompleted) {
            if(!sendFuelLevel()){
                return;
            }

            plane.land(runway);

            if(!sendPlaneLocation()){
                return;
            }

            if (plane.isLanded()) {
                isProcessCompleted = true;
                log.info("Plane [{}] has successfully landed on runway {{}]", plane.getId(), runway.getId());
            } else if (plane.getLocation().getAltitude() < 0){
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

    private void executeAlternative() {
        log.info("Plane [{}] instructed to HOLD_ALTERNATIVE_PATTERN", plane.getId());
        plane.holdAlternative();
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
        plane.destroyPlane();
        isProcessCompleted = true;
    }

    public static void main(String[] args) throws IOException {
        int numberOfClients = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);

        for (int i = 0; i < numberOfClients; i++) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            executorService.execute(client);
        }
        executorService.shutdown();
    }
}
