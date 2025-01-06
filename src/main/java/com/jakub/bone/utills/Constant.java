package com.jakub.bone.utills;

import com.jakub.bone.domain.airport.Location;

public class Constant {
    // Runway dimensions
    public static final int RUNWAY_WIDTH = 5000; // in meters
    public static final int RUNWAY_HEIGHT = 1000; // in meters

    // Airplane limitations
    public static final int MAX_CAPACITY = 100; // max number of airplanes in airspace
    public static final int MAX_ALTITUDE = 5000; // in meters
    public static final int MIN_ALTITUDE = 2000; // in meters
    public static final double ALTITUDE_COLLISION_DISTANCE = 10.0; // in meters
    public static final double HORIZONTAL_COLLISION_DISTANCE = 500.0; // in meters

    // Fuel-related values
    public static final double CONSUMPTION_PER_HOUR = 2000; // in liters
    public static final double INITIAL_FUEL_LEVEL = 6000; // for 3 hours in liters
    public static final double CONSUMPTION_PER_SECOND = CONSUMPTION_PER_HOUR / 3600.0; // in liters

    // Time-related values
    public static final long COLLISION_CHECK_INTERVAL = 1000; // in milliseconds

    // Landing-related values
    public static int LANDING_ALTITUDE = 700; // in meters
    public  static Location LANDING_POINT_RUNWAY_1 = new Location(3000, 1500, 0);
    public static Location LANDING_POINT_RUNWAY_2 = new Location(3000, -1500, 0);
    public static Location ENTRY_POINT_CORRIDOR_1 = new Location(-5000, 3500, 1000);
    public static Location ENTRY_POINT_CORRIDOR_2 = new Location(-5000, 500, 1000);
    public static Location FINAL_APPROACH_CORRIDOR_1 = new Location(-2000, 1500, 500);
    public static Location FINAL_APPROACH_CORRIDOR_2 = new Location(-2000, -1500, 500);


    // Holding-related
    public static int HOLDING_ALTITUDE = 1000; // in meters
    public static int HOLDING_ENTRY_ALTITUDE = 1013; // in meters
}
