package com.jakub.bone.application;

import lombok.extern.log4j.Log4j2;

import static com.jakub.bone.utills.Constant.COLLISION_CHECK_INTERVAL;

@Log4j2
public class CollisionDetector extends Thread {
    private ControlTower controlTower;
    public CollisionDetector(ControlTower controlTower){
        this.controlTower = controlTower;
    }

    @Override
    public void run() {
        while(true) {
            try {
                controlTower.checkCollision();
                log.info("Collision detector is working");
                Thread.sleep(COLLISION_CHECK_INTERVAL);
            } catch (InterruptedException ex){
                log.error("Collision detection interrupted: {}", ex.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}
