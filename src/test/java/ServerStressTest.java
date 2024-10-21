import server.AirportServer;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerStressTest {
    static final Logger logger = Logger.getLogger(ClientStressTest.class.getName());
    public static void main(String[] args) throws IOException {
        String outputDir = "C:\\Users\\Jakub Bone\\Desktop\\";
        String outputFileName = outputDir + "Server_error-log.txt";

        try{
            new FileWriter(outputFileName);
        } catch (IOException ex){
            logger.warning("Failed to create a file");
        }

        LoggerConfigurator.configureLogger("Server_error-log.txt");
        LoggerConfigurator.configureUncaughtExceptionHandler();

        AirportServer airportServer = new AirportServer();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Server stopped after 1 hour of operation");
                System.exit(0);

            }
        }, 10000);  // 10s

        try {
            airportServer.startServer(5000);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to start the server:", ex.getMessage());
        }
    }
}



