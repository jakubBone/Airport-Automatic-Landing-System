package controller;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CollisionDetector extends Thread {
    private ControlTower controller;
    private long checkIntervalMillis;
    public CollisionDetector(ControlTower controller, long checkIntervalMillis){
        this.controller = controller;
        this.checkIntervalMillis = checkIntervalMillis;
    }

    @Override
    public void run() {
        while(true) {
            try {
                controller.checkCollision();
                log.info("Collision detector is working");
                Thread.sleep(checkIntervalMillis);
            } catch (InterruptedException ex){
                log.error("Collision detection interrupted: {}", ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
