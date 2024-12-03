package plane;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class PlanePhase {
    public enum FlightPhase {
        DESCENDING,
        HOLDING,
        ALTERNATIVE_HOLDING,
        LANDING
    }
    private FlightPhase phase;
    public PlanePhase() {
        this.phase = FlightPhase.DESCENDING;
    }

    public void changePhase(FlightPhase newPhase) {
        this.phase = newPhase;
    }
}
