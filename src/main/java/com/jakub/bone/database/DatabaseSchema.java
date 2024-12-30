package com.jakub.bone.database;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class DatabaseSchema {
    private final DSLContext CONTEXT;
    public DatabaseSchema(DSLContext dsl) {
        this.CONTEXT = dsl;
        createTables();
    }

    public void createTables() {
        CONTEXT.createTableIfNotExists("planes")
                .column("id", SQLDataType.INTEGER.identity(true))
                .column("flight_number", SQLDataType.VARCHAR)
                .column("start_time", SQLDataType.LOCALDATETIME(1).nullable(false))
                .column("landing_time", SQLDataType.LOCALDATETIME(1))
                .constraints(
                        DSL.constraint("PK_PLANES").primaryKey("id"))
                .execute();

        CONTEXT.createTableIfNotExists("collisions")
                .column("id", SQLDataType.INTEGER.identity(true))
                .column("involved_planes", SQLDataType.INTEGER.getArrayDataType())
                .column("time", SQLDataType.LOCALDATETIME(1))
                .constraints(
                        DSL.constraint("PK_COLLISIONS").primaryKey("id"))
                .execute();
    }

    public void clearTables(){
        CONTEXT.truncate("planes").execute();
        CONTEXT.truncate("collisions").execute();
    }
}