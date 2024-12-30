package com.jakub.bone.application;

import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.domain.plane.Plane;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import static com.jakub.bone.utills.Constant.*;

@Log4j2
public class CollisionDetector extends Thread {
    private ControlTower controlTower;
    public CollisionDetector(ControlTower controlTower){
        this.controlTower = controlTower;
    }

    @Override
    public void run() {
        ThreadContext.put("type", "Server");
        while(true) {
            try {
                checkCollision();
                Thread.sleep(COLLISION_CHECK_INTERVAL);
            } catch (InterruptedException ex){
                log.error("Collision detection interrupted: {}", ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    public void checkCollision() {
            for (int i = 0; i < controlTower.getPlanes().size(); i++) {
                Plane plane1 = controlTower.getPlanes().get(i);
                for (int j = i + 1; j < controlTower.getPlanes().size(); j++) {
                    Plane plane2 = controlTower.getPlanes().get(j);
                    if (arePlanesToClose(plane1.getNavigator().getLocation(), plane2.getNavigator().getLocation())) {
                        handleCollision(plane1, plane2);
                    }
                }
            }
    }

    private void handleCollision(Plane plane1, Plane plane2){
        String[] collidedIDs = {plane1.getFlightNumber(), plane2.getFlightNumber()};
        controlTower.getDatabase().getCOLLISION_DAO().registerCollisionToDB(collidedIDs);
        plane1.setDestroyed(true);
        plane2.setDestroyed(true);
        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getFlightNumber(), plane2.getFlightNumber());
    }

    /*
     * Checks if two planes (calculated from their center points) are too close to each other.
     *
     * The 500 meters in the X–Y regards to the model size of implemented plane.obj
     * providing a safety margin for plane length wings, etc.
     *
     * In practice, if the horizontal distance <= 500 and the altitude difference <= 10,
     * it is considered a potential collision risk
     */
    private boolean arePlanesToClose(Location loc1, Location loc2) {
        double horizontalDistance = Math.sqrt(
                Math.pow(loc1.getX() - loc2.getX(), 2) +
                        Math.pow(loc1.getY() - loc2.getY(), 2)
        );
        double altDiff = Math.abs(loc1.getAltitude() - loc2.getAltitude());
        return horizontalDistance <= HORIZONTAL_COLLISION_DISTANCE && altDiff <= ALTITUDE_COLLISION_DISTANCE;
    }
}
