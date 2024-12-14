package controller;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CollisionDetector extends Thread{
    private AirTrafficController controller;
    public CollisionDetector(AirTrafficController controller){
        this.controller = controller;
    }

    @Override
    public void run() {
        while(true) {
            try {
                controller.checkCollision();
                log.info("Collision detector is working");
                Thread.sleep(1200);
            } catch (InterruptedException ex){
                log.error("Collision detection interrupted: {}", ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
