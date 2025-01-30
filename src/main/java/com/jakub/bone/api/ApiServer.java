package com.jakub.bone.api;

import com.jakub.bone.api.get.CollisionsAirportServlet;
import com.jakub.bone.api.get.PlanesAirportServlet;
import com.jakub.bone.api.get.UptimeAirportServlet;
import com.jakub.bone.api.post.PauseAirportServlet;
import com.jakub.bone.api.post.StartAirportServlet;
import com.jakub.bone.api.post.StopAirportServlet;
import com.jakub.bone.api.post.ResumeAirportServlet;
import com.jakub.bone.server.AirportServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.IOException;
import java.sql.SQLException;

public class ApiServer {
    public static void main(String[] args) throws SQLException {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler();
        server.setHandler(context);

        AirportServer airportServer = new AirportServer();
        context.setAttribute("airportServer", airportServer);

        // Init Servlets
        context.addServlet(new ServletHolder(new StartAirportServlet()), "/airport/start");
        context.addServlet(new ServletHolder(new PauseAirportServlet()), "/airport/pause");
        context.addServlet(new ServletHolder(new ResumeAirportServlet()), "/airport/resume");
        context.addServlet(new ServletHolder(new StopAirportServlet()), "/airport/stop");
        context.addServlet(new ServletHolder(new UptimeAirportServlet()), "/airport/uptime");
        context.addServlet(new ServletHolder(new PlanesAirportServlet()), "/airport/planes/*");
        context.addServlet(new ServletHolder(new CollisionsAirportServlet()), "/airport/collisions");

        try {
            server.start();
            System.out.println("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("API Server interrupted: " + ex.getMessage());
            }
        } catch (IOException ex) {
            System.err.println("Unexpected error: " + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                System.err.println("Failed to stop server: " + e.getMessage());
            }
        }
    }
}
