import client.PlaneClient;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ClientStressTest {

    // Ustawiamy logger
    static final Logger logger = Logger.getLogger(ClientStressTest.class.getName());

    public static void main(String[] args) {
        configureLogger();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Client stopped after 10 seconds of operation");
                System.exit(0);
            }
        }, 10000);  // Zakończ po 10 sekundach

        ExecutorService executorService = Executors.newCachedThreadPool();

        while (true) {
            try {
                PlaneClient client = new PlaneClient("localhost", 5000);
                executorService.execute(client);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, "Client thread interrupted", ex);
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "An error occurred", e);
            }
        }
    }

    private static void configureLogger() {
        // Ścieżka do pliku logowania
        String outputFileName = "C:\\Users\\Jakub Bone\\Desktop\\Client_error.txt";  // Ścieżka do pliku logów

        try {
            // FileHandler do zapisu logów do pliku
            FileHandler fileHandler = new FileHandler(outputFileName, false);
            fileHandler.setFormatter(new SimpleFormatter());

            // Poziom zapisywania logów do pliku
            fileHandler.setLevel(Level.WARNING);  // Zapisuje poziomy od WARNING w górę (WARNING i SEVERE)

            // Dodajemy handler do loggera
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);  // Może logować wszystkie poziomy, ale zapisuje od WARNING wzwyż

            // Globalny rejestrator nieobsłużonych wyjątków
            Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
                logger.log(Level.SEVERE, "Uncaught exception in thread: " + thread.getName(), exception);
            });
        } catch (IOException e) {
            System.err.println("Nie udało się skonfigurować loggera: " + e.getMessage());
        }
    }
}

