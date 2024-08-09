package airport;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class Runway {
    private String id;
    public Runway(String id) {
        this.id = id;
    }

}
