package database;

import connection_pool.ConnectionPool;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.SQLException;

public class AirportDatabase {
    private DSLContext context;
    private DatabaseConnection conn;
    private ConnectionPool pool;
    private DatabaseSchema schema;

    public AirportDatabase() throws SQLException {
        this.conn = new DatabaseConnection("airport", "plane123", "airport_system", 5432);
        this.context = DSL.using(conn.getConnection());
        this.pool = new ConnectionPool(10, 100, conn);
        this.schema = new DatabaseSchema(context);
    }

}
