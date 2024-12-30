package com.jakub.bone.domain.airport;

import com.jakub.bone.domain.airport.Corridor;
import com.jakub.bone.domain.airport.Location;
import com.jakub.bone.utills.Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.Serializable;

@Log4j2
@Getter
@Setter
public class Runway implements Serializable {
    private String id;
    private Location landingPoint;
    private Corridor corridor;
    private boolean available;

    public Runway(String id, Location landingPoint, Corridor corridor) {
        this.id = id;
        this.landingPoint = landingPoint;
        this.corridor = corridor;
        this.available = false;
    }
}
