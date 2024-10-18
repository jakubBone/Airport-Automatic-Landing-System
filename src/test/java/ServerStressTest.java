import server.AirportServer;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerStressTest {
    public static void main(String[] args) {
        AirportServer airportServer = new AirportServer();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Server stopped after 1 hour of operation");
                System.exit(0);

            }
        }, 3600000);  // 1h = 3600000ms

        try {
            airportServer.startServer(5000);
        } catch (Exception ex) {
            System.err.println("Failed to start the server: " + ex.getMessage());
        }
    }
}



