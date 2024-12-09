package database;

import connection_pool.ConnectionPool;

public class Database {
    DatabaseConnection conn = new DatabaseConnection("airport", "plane123", "airport_system", 5432);
    ConnectionPool pool = new ConnectionPool(10, 100, conn);
}
