package com.jakub.bone.api;

import com.google.gson.Gson;
import com.jakub.bone.client.PlaneClient;
import com.jakub.bone.server.AirportServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.jakub.bone.utills.Constant.CLIENT_SPAWN_DELAY;
import static com.jakub.bone.utills.Constant.SERVER_INIT_DELAY;

@WebServlet(urlPatterns = "/airport/start")
public class StartAirportServlet extends HttpServlet {
    private static AirportServer airportServer;
    private static Lock lock = new ReentrantLock();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean started = false;
        lock.lock();
        try {
            if (airportServer != null && airportServer.isRunning()) {
                started = false;
            } else {
                startAirport();
                started = true;
            }
        } finally {
            lock.unlock();
        }

        String json;
        if (started) {
            json = new Gson().toJson("{\"message\": \"Airport started successfully\"}");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            json = new Gson().toJson("{\"message\": \"Airport is already running\"}");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
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
                    //log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
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
                        //log.error("Collision detection interrupted: {}", ex.getMessage(), ex);
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }
    }
}
