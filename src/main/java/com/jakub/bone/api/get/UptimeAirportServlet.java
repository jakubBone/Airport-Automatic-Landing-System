package com.jakub.bone.api.get;

import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/airport/uptime")
public class UptimeAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
            if (airportServer != null || airportServer.getStartTime() == null) {
                messenger.send(response, Map.of("message", "airport is not running"));
                return;
            }

            Duration uptime = Duration.between(airportServer.getStartTime(),Instant.now());
            long hours = uptime.toHours();
            long minutes = uptime.toMinutesPart();
            long seconds = uptime.toSecondsPart();

            messenger.send(response, Map.of("message", String.format("%02d:%02d:%02d", hours, minutes, seconds)));
        } catch (Exception ex){
            messenger.send(response, Map.of("error", "Failed to retrieve uptime"));
            System.err.println("Error retrieving update data: " + ex.getMessage());
        }
    }
}
