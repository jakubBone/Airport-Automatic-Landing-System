package client;

import airport.Runway;
import handler.PlaneHandler;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static handler.PlaneHandler.Instruction.DESCENT;
import static handler.PlaneHandler.Instruction.HOLD_PATTERN;
import static handler.PlaneHandler.Instruction.LAND;
import static handler.PlaneHandler.Instruction.FULL;

@Log4j2
public class PlaneClient extends Client  {
    private Plane plane;
    private boolean isProcessCompleted;
    private Lock lock;

    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane();
        lock = new ReentrantLock();
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

                out.reset();
                out.writeObject(plane.getLocation());
                out.flush();

                String instruction = (String) in.readObject();
                processAirportInstruction(instruction);

                if(plane.isLanded()){
                    log.info("Plane [{}] successfully landed", plane.getId());
                    isProcessCompleted = true;
                }

                Thread.sleep(1000);
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to handle communication with plane [{}]: {}", plane.getId(), ex.getMessage());
        } finally {
            log.info("Plane [{}] exited communication", plane.getId());
            stopConnection();
        }
    }

    private void processAirportInstruction(String instruction) throws IOException, ClassNotFoundException {
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
                Runway runway = (Runway) in.readObject();
                log.info("Plane [{}] instructed to LAND on an available runway {{}]", plane.getId(), runway.getId());
                plane.setLandingPhase(runway);
                while (!plane.isLanded()) {
                    plane.land(runway);
                    out.writeObject(plane.getLocation());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        log.error("Landing process interrupted for Plane [{}]", plane.getId());
                    }
                }
                break;
            case FULL:
                log.info("Airspace is FULL. Plane [{}] cannot land. Searching for alternative airport", plane.getId());
                isProcessCompleted = true;
                break;
            default:
                log.warn("Unknown instruction for Plane [{}]: [{}]", plane.getId(), instruction);
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                PlaneClient client = new PlaneClient("localhost", 5000);
                client.startCommunication();
            }).start();
        }

    }
}
