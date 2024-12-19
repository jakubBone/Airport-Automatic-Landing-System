package load_tests;

import controller.ControlTower;
import database.AirportDatabase;
import server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerLoadTest {
    static final Logger logger = Logger.getLogger(ClientLoadTest.class.getName());
    public static void main(String[] args) throws IOException, SQLException {
        AirportDatabase airportDatabase = new AirportDatabase();
        ControlTower controller = new ControlTower(airportDatabase);

        AirportServer airportServer = null;
        try {
            airportServer = new AirportServer(controller);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        airportServer = new AirportServer(controller);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Server stopped after 70 minutes");
                System.exit(0);

            }
        }, 4200000);  // 10s

        try {
            airportServer.startServer(5000);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to start the server:", ex.getMessage());
        }
    }
}