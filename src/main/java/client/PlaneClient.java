package client;

import utills.Messenger;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


@Log4j2
public class PlaneClient extends Client implements Runnable {
    private Plane plane;
    private Messenger messenger;
    private PlaneInstructionHandler instructionHandler;
    private PlaneCommunicationService communicationService;

    public PlaneClient(String ip, int port) {
        super(ip, port);
        this.plane = new Plane(generateFlightNumber());
        this.messenger = new Messenger();
        log.info("PlaneClient created for Plane [{}] at IP: {}, Port: {}", plane.getFlightNumber(), ip, port);
    }

    @Override
    public void run() {
        try {
            startConnection();
            if(!isConnected){
                return;
            }

            initializeServices();
            communicationService.sendInitialData();

            handleInstructions();

        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}]: Communication failure: {}", plane.getFlightNumber(), ex.getMessage());
        } finally {
            log.info("Plane [{}] exited communication", plane.getFlightNumber());
            stopConnection();
        }
    }

    private void initializeServices(){
        this.communicationService = new PlaneCommunicationService(plane, messenger, out);
        this.instructionHandler = new PlaneInstructionHandler(plane, messenger, in, out);
    }

    private void handleInstructions() throws IOException, ClassNotFoundException {
        while (!instructionHandler.isProcessCompleted()) {
            if(!communicationService.sendFuelLevel() || !communicationService.sendPlaneLocation()){
                return;
            }

            instructionHandler.processInstruction();

            if(plane.isDestroyed()){
                return;
            }
        }
    }

    public String generateFlightNumber() {
        String[] airlineCodes = {"MH", "AA", "BA", "LH", "AF", "EK", "QR", "KL", "UA", "DL"};

        String code = airlineCodes[ThreadLocalRandom.current().nextInt(airlineCodes.length)];

        int number = ThreadLocalRandom.current().nextInt(100, 999);

        return code + number;
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
