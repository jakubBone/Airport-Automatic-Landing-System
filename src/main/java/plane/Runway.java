package plane;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class Runway {
    private String id;
    public Runway(String id) {
        this.id = id;
    }
    public void landPlane(Plane plane){
        try{
            System.out.println(plane.getPlaneId() + "Plane landing on runway: " + id);
            Thread.sleep(1000);
            System.out.println(plane.getPlaneId() + " Landing...");
            Thread.sleep(1000);
            System.out.println(plane.getPlaneId() + " Plane landed");
        } catch (InterruptedException ex){
            log.error("Error occurred while waiting for landing: {}", ex.getMessage());
        }

    }


}
