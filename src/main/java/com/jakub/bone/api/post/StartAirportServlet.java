package com.jakub.bone.api.post;

import com.jakub.bone.client.PlaneClient;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.jakub.bone.utills.Constant.CLIENT_SPAWN_DELAY;
import static com.jakub.bone.utills.Constant.SERVER_INIT_DELAY;

@WebServlet(urlPatterns = "/airport/start")
public class StartAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    private Lock lock = new ReentrantLock();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        lock.lock();
        try {
            if (airportServer != null && airportServer.isRunning()) {
                messenger.send(response, Map.of("message", "airport is already running"));
            } else {
                startAirport();
                messenger.send(response, Map.of("message", "airport started successfully"));
            }
        } catch (Exception ex){
            messenger.send(response, Map.of("error", "Failed to start airport"));
            System.err.println("Error starting airport: " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void startAirport() {
        if(airportServer == null) {
            Thread serverThread = new Thread(() -> {
                try {
                    this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
                    this.airportServer.startServer(5000);
                }  catch (IOException ex) {
                    throw new RuntimeException("Failed to initialize AirportServer due to I/O issues", ex);
                }
            });
            serverThread.start();

            // Wait for the server to initialize before proceeding
            while (airportServer == null || airportServer.getControlTower() == null) {
                try {
                    Thread.sleep(SERVER_INIT_DELAY);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    PlaneClient client = new PlaneClient("localhost", 5000);
                    new Thread(client).start();

                    try {
                        Thread.sleep(CLIENT_SPAWN_DELAY);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }
}
