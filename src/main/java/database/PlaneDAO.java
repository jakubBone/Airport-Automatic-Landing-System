package database;

import org.jooq.DSLContext;
import plane.Plane;

import java.time.LocalDateTime;

import static org.jooq.impl.DSL.*;

public class PlaneDAO {
    private final DSLContext CONTEXT;
    public PlaneDAO(DSLContext context) {
        CONTEXT = context;
    }

    public void registerPlaneInDB(Plane plane){
        CONTEXT.insertInto(table("planes"),
                    field("id"),
                    field("start_time"))
                .values(plane.getId(),
                    LocalDateTime.now())
                .execute();
    }

    public void registerLandingInDB(Plane plane){
        CONTEXT.update(table("planes"))
                .set(field("land_time"), LocalDateTime.now())
                .where(field("id").eq(plane.getId()))
                .execute();
    }
}
