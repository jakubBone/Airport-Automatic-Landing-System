package com.jakub.bone.dbinit;

public class DbConstants {

    public final static String USER = "airport";
    public final static String PASSWORD = "plane123";
    public final static String DATABASE = "airport_system";
    public final static int DATABASE_PORT = 5432;
    public final static String URL = String.format("jdbc:postgresql://localhost:%d/%s", DATABASE_PORT, DATABASE);

}
