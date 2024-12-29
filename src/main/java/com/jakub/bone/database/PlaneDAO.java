package com.jakub.bone.database;

import org.jooq.DSLContext;
import com.jakub.bone.domain.plane.Plane;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;

public class PlaneDAO {
    private final DSLContext CONTEXT;
    public PlaneDAO(DSLContext context) {
        CONTEXT = context;
    }

    public void registerPlaneInDB(Plane plane){
        CONTEXT.insertInto(table("planes"),
                    field("flight_number"),
                    field("start_time"))
                .values(plane.getFlightNumber(),
                    LocalDateTime.now())
                .execute();
    }

    public void registerLandingInDB(Plane plane){
        CONTEXT.update(table("planes"))
                .set(field("landing_time"), LocalDateTime.now())
                .where(field("flight_number").eq(plane.getFlightNumber()))
                .execute();
    }
}
