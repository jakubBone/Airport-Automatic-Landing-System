import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;
public class LoggerConfigurator {

    static final Logger logger = Logger.getLogger(ClientStressTest.class.getName());

    static void configureLogger(String logFileName) {
        try {
            String logFilePath = "C:\\Users\\Jakub Bone\\Desktop\\" + logFileName;

            FileHandler fileHandler = new FileHandler(logFilePath, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (Exception ie) {
            logger.log(Level.SEVERE, "Failed to configure logger", ie);
        }
    }

    static void configureUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.log(Level.SEVERE, "Uncaught exception in thread: " + thread.getName(), throwable);
        });
    }
}
