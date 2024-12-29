package controller;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CollisionDetector implements Runnable {
    private ControlTower controller;
    private long checkIntervalMillis;
    private boolean isRunning;
    public CollisionDetector(ControlTower controller, long checkIntervalMillis){
        this.controller = controller;
        this.checkIntervalMillis = checkIntervalMillis;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while(isRunning) {
            try {
                controller.checkCollision();
                log.info("Collision detector is working");
                Thread.sleep(1200);
            } catch (InterruptedException ex){
                log.error("Collision detection interrupted: {}", ex.getMessage());
                Thread.currentThread().interrupt();
                stop();
            }
        }
    }
    public void stop() {
        isRunning = false;
    }
}
