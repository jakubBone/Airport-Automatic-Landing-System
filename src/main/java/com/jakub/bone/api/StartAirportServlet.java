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
import java.sql.SQLException;

import static com.jakub.bone.utills.Constant.CLIENT_SPAWN_DELAY;
import static com.jakub.bone.utills.Constant.SERVER_INIT_DELAY;

@WebServlet(urlPatterns = "/airport/start")
public class StartAirportServlet extends HttpServlet {
    private static AirportServer airportServer;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        startAirport();
        String json = new Gson().toJson("{\"message\": \"Airport started successfully\"}");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(json);
    }
    public void startAirport() {
        Thread serverThread = new Thread(() -> {
            try {
                this.airportServer = new AirportServer();
                this.airportServer.startServer(5000);
            } catch (SQLException ex) {
                throw new RuntimeException("Failed to initialize AirportServer due to database issues", ex);
            } catch (IOException ex) {
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
