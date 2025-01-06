package com.jakub.bone.database;

import org.jooq.DSLContext;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;

public class CollisionRepository {
    private final DSLContext CONTEXT;
    public CollisionRepository(DSLContext CONTEXT) {
        this.CONTEXT = CONTEXT;
    }

    public void registerCollisionToDB(String [] planesIds){
        CONTEXT.insertInto(table("collisions"),
                    field("involved_planes"),
                    field("time"))
                .values(planesIds,
                        LocalDateTime.now())
                .execute();
    }
}
