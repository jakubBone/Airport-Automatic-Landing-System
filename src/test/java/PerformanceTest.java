import client.PlaneClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest {
    @Test
    public void testStressLandingSystem()  {
        int numberOfClients = 150;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfClients);
        List<PlaneClient> clients = new ArrayList<>();

        for (int i = 0; i < numberOfClients; i++) {
            PlaneClient client = new PlaneClient("localhost", 5000);
            clients.add(client);
            executorService.execute(client);
        }
        executorService.shutdown();

        assertTrue(clients.size() < 100);
    }
}

