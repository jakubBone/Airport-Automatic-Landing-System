package database;

import org.jooq.DSLContext;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;


public class CollisionDAO {
    private final DSLContext CONTEXT;

    public CollisionDAO(DSLContext CONTEXT) {
        this.CONTEXT = CONTEXT;
    }

    public void registerCollisionToDB(String [] planesIds){
        CONTEXT.insertInto(table("collisions"),
                    field("involved_planes"),
                    field("collision_time"))
                .values(planesIds,
                        LocalDateTime.now())
                .execute();
    }
}
