import client.PlaneClient;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientStressTest {
    public static void main(String[] args) throws IOException {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Client stopped after 1 hour of operation");
                System.exit(0);

            }
        }, 3600000);  // 1h = 3600000ms


        ExecutorService executorService = Executors.newCachedThreadPool();

        while(true) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            executorService.execute(client);
            try{
                Thread.sleep(5000);
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }

    }
}
