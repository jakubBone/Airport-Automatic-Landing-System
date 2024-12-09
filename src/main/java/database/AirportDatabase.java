package database;

import connection_pool.ConnectionPool;
import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.SQLException;

@Getter
public class AirportDatabase {
    private DatabaseConnection conn;
    private DSLContext context;
    private DatabaseSchema schema;
    private PlaneDAO planeDAO;
    private ConnectionPool pool;

    public AirportDatabase() throws SQLException {
        this.conn = new DatabaseConnection("airport", "plane123", "airport_system", 5432);
        this.context = DSL.using(conn.getConnection());
        this.planeDAO = new PlaneDAO(context);
        this.schema = new DatabaseSchema(context);
        this.pool = new ConnectionPool(10, 100, conn);
    }
}
