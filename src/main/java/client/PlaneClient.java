package client;

import controller.PlaneHandler;
import lombok.Getter;
import utills.Messenger;
import lombok.extern.log4j.Log4j2;
import plane.Plane;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;


@Log4j2
@Getter
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
        connectAndHandle();
    }

    private void connectAndHandle(){
        try {
            establishConnection();
            initializeServices();
            communicationService.sendInitialData();
            processInstructions();
        } catch (IOException | ClassNotFoundException ex) {
            log.error("PlaneClient [{}] encountered an error: {}", plane.getFlightNumber(), ex.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void establishConnection() throws IOException {
        startConnection();
        if (!isConnected) {
            throw new IOException("Unable to establish connection to the server");
        }
    }

    private void initializeServices(){
        this.communicationService = new PlaneCommunicationService(plane, messenger, out);
        this.instructionHandler = new PlaneInstructionHandler(plane, messenger, in, out);
    }

    private void processInstructions() throws IOException, ClassNotFoundException {
        while (!instructionHandler.isProcessCompleted()) {
            if(!communicationService.sendFuelLevel() || !communicationService.sendLocation()){
                log.warn("Plane [{}] lost communication due to fuel or location issues", plane.getFlightNumber());
                return;
            }

            PlaneHandler.AirportInstruction instruction = messenger.receiveAndParse(in, PlaneHandler.AirportInstruction.class);
            instructionHandler.processInstruction(instruction);

            if(plane.isDestroyed()){
                log.warn("Plane [{}] destroyed", plane.getFlightNumber());
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

    private void closeConnection() {
        log.info("Plane [{}] exiting communication.", plane.getFlightNumber());
        stopConnection();
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
