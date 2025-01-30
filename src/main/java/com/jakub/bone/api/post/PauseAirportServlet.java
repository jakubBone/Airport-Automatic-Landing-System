package com.jakub.bone.api.post;

import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/pause")
public class PauseAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        try {
            if (airportServer != null && airportServer.isPaused()) {
                messenger.send(response, Map.of("message", "airport is already paused"));
            } else {
                airportServer.pauseServer();
                messenger.send(response, Map.of("message", "airport paused successfully"));
            }
        } catch (Exception ex){
            messenger.send(response, Map.of("error", "Failed to pause airport"));
            System.err.println("Error pausing airport: " + ex.getMessage());
        }
    }
}
