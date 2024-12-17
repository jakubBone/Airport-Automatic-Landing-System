package client;

import airport.Runway;
import controller.PlaneHandler;
import lombok.Getter;
import plane.Plane;
import utills.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


@Log4j2
@Getter
public class PlaneInstructionHandler {
    private Plane plane;
    private Messenger messenger;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean isProcessCompleted;
    private PlaneCommunicationService communicationService;

    public PlaneInstructionHandler(Plane plane, Messenger messenger, ObjectInputStream in, ObjectOutputStream out) {
        this.plane = plane;
        this.messenger = messenger;
        this.in = in;
        this.out = out;
        this.communicationService = new PlaneCommunicationService(plane, messenger, out);
    }

    public void processInstruction() throws IOException, ClassNotFoundException {
        PlaneHandler.AirportInstruction instruction = messenger.receiveAndParse(in, PlaneHandler.AirportInstruction.class);
        switch (instruction) {
            case DESCENT -> executeDescent();
            case HOLD_PATTERN -> executeHoldPattern();
            case LAND -> processLanding();
            case FULL -> executeFullAirspace();
            case OCCUPIED -> executeOccupiedLocation();
            case COLLISION -> executeCollision();
            default -> log.warn("Unknown instruction for Plane [{}]: [{}]", plane.getFlightNumber(), instruction);
        }
    }

    public void processLanding() throws IOException, ClassNotFoundException {
        Runway runway = messenger.receiveAndParse(in, Runway.class);
        plane.setLandingPhase(runway);

        log.info("Plane [{}] assigned to LAND on runway {{}]", plane.getFlightNumber(), runway.getId());
        while (!isProcessCompleted) {

            if(!communicationService.sendFuelLevel()){
                return;
            }

            plane.land(runway);

            if(!communicationService.sendPlaneLocation()){
                return;
            }

            if (plane.isLanded()) {
                isProcessCompleted = true;
                log.info("Plane [{}] has successfully landed on runway {{}]", plane.getFlightNumber(), runway.getId());
            }
        }
    }

    private void executeDescent() {
        log.info("Plane [{}] instructed to DESCENT", plane.getFlightNumber());
        plane.descend();
    }

    private void executeHoldPattern() {
        log.info("Plane [{}] instructed to HOLD_PATTERN", plane.getFlightNumber());
        plane.hold();
    }

    private void executeFullAirspace() {
        log.info("Airspace is FULL. Plane [{}] instructed to find an alternative airport. Stopping communication", plane.getFlightNumber());
        isProcessCompleted = true;
    }

    private void executeOccupiedLocation() {
        log.info("Initial location OCCUPIED. Plane [{}] cannot be registered in the location. Stopping communication", plane.getFlightNumber());
        isProcessCompleted = true;
    }

    private void executeCollision() {
        log.info("COLLISION detected for Plane [{}]. Stopping communication", plane.getFlightNumber());
        plane.destroyPlane();
        isProcessCompleted = true;
    }
}
