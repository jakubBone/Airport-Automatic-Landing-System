package client;

import airport.Runway;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
                plane.holdPattern();

                if (plane.isOutOfFuel()) {
                    log.info("Plane [{}] is out of fuel, exiting communication loop", plane.getId());
                    break;
                }

                out.reset();
                out.writeObject(plane.getLocation());
                out.flush();

                String instruction = (String) in.readObject();
                //log.info("Received instruction [{}] for Plane [{}]", instruction, plane.getId());
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

    private void processAirportInstruction(String instruction) throws IOException, ClassNotFoundException {
        switch (instruction) {
            case "WAIT":
                log.info("Plane [{}] instructed to [{}] for an available runway", plane.getId(), instruction);
                break;
            case "LAND":
                log.info("Plane [{}] instructed to [{}] on an available runway", plane.getId(), instruction);
                processLanding();
                isProcessCompleted = true;
                break;
            case "FULL":
                log.info("Airspace is [{}]. Plane [{}] cannot land. Search for an alternative airport.", instruction, plane.getId());                isProcessCompleted = true;
                isProcessCompleted = true;
                break;
            default:
                log.warn("Plane [{}] received an unknown instruction: [{}]", plane.getId(), instruction);
                break;
        }
    }

    private void processLanding() throws IOException, ClassNotFoundException {
        Runway assignedRunway = (Runway) in.readObject();
        log.info("Plane [{}] assigned to land on runway [{}]", plane.getId(), assignedRunway.getId());
        while (!plane.isLanded()) {
            out.writeObject(plane.getLocation());
            plane.proceedToLand(assignedRunway);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Landing process interrupted for Plane [{}]", plane.getId());
            }
        }
        log.info("Plane [{}] successfully landed on runway [{}]", plane.getId(), assignedRunway.getId());
    }

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 50; i++) {
            new Thread(() -> {
                PlaneClient client = new PlaneClient("localhost", 5000);
                client.startCommunication();
            }).start();
        }

    }
}
