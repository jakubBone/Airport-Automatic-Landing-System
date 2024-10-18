import client.PlaneClient;

import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

class ClientStressTest {
    static final Logger logger = Logger.getLogger(ClientStressTest.class.getName());

    public static void main(String[] args) throws IOException {
        LoggerConfigurator.configureLogger("Client_error-log.txt");
        LoggerConfigurator.configureUncaughtExceptionHandler();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Client stopped after 1 hour of operation");
                System.exit(0);
            }
        }, 30000);  // 30s

        ExecutorService executorService = Executors.newCachedThreadPool();

        while(true) {
            try{
                PlaneClient client = new PlaneClient("localhost", 5000);
                executorService.execute(client);
                try{
                    Thread.sleep(5000);
                } catch (InterruptedException ex){
                    logger.log(Level.WARNING, "Client thread interrupted", ex);
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e){
               logger.log(Level.SEVERE, "An error occurred", e);
            }
        }
    }


}
