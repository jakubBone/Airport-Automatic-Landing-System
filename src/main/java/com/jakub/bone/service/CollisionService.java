package com.jakub.bone.service;

import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.domain.plane.Plane;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.jakub.bone.config.Constant.*;

@Log4j2
public class CollisionService {
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ControlTowerService controlTowerService;
    private final ScheduledExecutorService scheduler;

    public CollisionService(ControlTowerService controlTowerService) {
        this.controlTowerService = controlTowerService;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "CollisionDetector");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void start() {
        scheduler.scheduleAtFixedRate(
                this::detectCollisionSafely,
                0,
                COLLISION_CHECK_DELAY,
                TimeUnit.MILLISECONDS
        );
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                log.warn("Collision service forced shutdown after timeout");
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void detectCollisionSafely() {
        try {
            ThreadContext.put("type", "Server");
            detectCollision();
        } catch (Exception ex) {
            log.error("Error during collision detection: {}", ex.getMessage(), ex);
        }
    }

    public void detectCollision() {
        List<Plane> planes = controlTowerService.getPlanesSnapshot();

        for (int i = 0; i < planes.size(); i++) {
            Plane plane1 = planes.get(i);
            for (int j = i + 1; j < planes.size(); j++) {
                Plane plane2 = planes.get(j);
                if (arePlanesTooClose(plane1.getNavigator().getLocation(), plane2.getNavigator().getLocation())) {
                    handleCollision(plane1, plane2);
                }
            }
        }
    }

    private void handleCollision(Plane plane1, Plane plane2) {
        String[] collidedIDs = {plane1.getFlightNumber(), plane2.getFlightNumber()};
        controlTowerService.getDatabase().getCOLLISION_REPOSITORY().registerCollisionToDB(collidedIDs);
        plane1.setDestroyed(true);
        plane2.setDestroyed(true);
        log.info("Collision detected between Plane [{}] and Plane [{}]", plane1.getFlightNumber(), plane2.getFlightNumber());
    }

    /*
     * Checks if two planes are too close to each other
     *
     * Because the animation uses scaled aircraft models, the system has been adjusted to their size
     * Introduced a 500-meter offset, ensuring collisions visually occur when the models actually touch
     *
     * In practice, if the horizontal distance <= 500 and the altitude difference <= 10,
     * it is considered a potential collision risk
     */
    private boolean arePlanesTooClose(Location loc1, Location loc2) {
        double horizontalDistance = Math.sqrt(
                Math.pow(loc1.getX() - loc2.getX(), 2) +
                        Math.pow(loc1.getY() - loc2.getY(), 2)
        );
        double altDiff = Math.abs(loc1.getAltitude() - loc2.getAltitude());
        return horizontalDistance <= HORIZONTAL_COLLISION_DISTANCE && altDiff <= ALTITUDE_COLLISION_DISTANCE;
    }
}
