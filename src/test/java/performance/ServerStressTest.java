package performance;

import airport.AirTrafficController;
import server.AirportServer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerStressTest {
    static final Logger logger = Logger.getLogger(ClientStressTest.class.getName());
    public static void main(String[] args) throws IOException {
        AirTrafficController controller = new AirTrafficController();

        AirportServer airportServer = new AirportServer(controller);
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