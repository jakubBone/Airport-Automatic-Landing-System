package client;

import plane.Plane;
import utills.Messenger;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectOutputStream;

@Log4j2
public class PlaneCommunicationService {
    private Plane plane;
    private Messenger messenger;
    private ObjectOutputStream out;

    public PlaneCommunicationService(Plane plane, Messenger messenger, ObjectOutputStream out) {
        this.plane = plane;
        this.messenger = messenger;
        this.out = out;
    }

    public void sendInitialData() throws IOException {
        messenger.send(plane, out);
    }

    public boolean sendFuelLevel() throws IOException {
        messenger.send(plane.getFuelManager().getFuelLevel(), out);
        out.flush();

        if (plane.getFuelManager().isOutOfFuel()) {
            log.info("Plane [{}] is out of fuel. Collision", plane.getId());
            return false;
        }
        return true;
    }

    public boolean sendPlaneLocation() throws IOException {
        if(plane.getNavigator().getLocation() == null) {
            log.info("Plane [{}] disappeared from the radar", plane.getId());
            return false;
        }
        messenger.send(plane.getNavigator().getLocation(), out);
        out.flush();
        return true;
    }
}
