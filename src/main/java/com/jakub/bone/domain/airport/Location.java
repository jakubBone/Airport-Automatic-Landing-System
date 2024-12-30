package com.jakub.bone.domain.airport;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
public class Location implements Serializable {
    int x;
    int y;
    int altitude;
    public Location(int x, int y, int altitude) {
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Location location = (Location) obj;
        return x == location.x && y == location.y && altitude == location.altitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, altitude);
    }
}