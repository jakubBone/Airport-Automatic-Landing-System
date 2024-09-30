package collision;

import airport.AirTrafficController;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class CollisionDetector extends Thread{
    private AirTrafficController controller;
    public CollisionDetector(AirTrafficController controller){
        this.controller = new AirTrafficController();
    }

    @Override
    public void run() {
        while(true) {
            try {
                controller.checkCollision();
                Thread.sleep(1000);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
                log.error("Collision detection interrupted: {}", ex.getMessage());
            }
        }
    }
}
