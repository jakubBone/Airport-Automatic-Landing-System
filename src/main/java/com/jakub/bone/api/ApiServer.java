package com.jakub.bone.api;

import com.jakub.bone.server.AirportServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.sql.SQLException;

public class ApiServer {
    public static void main(String[] args) throws SQLException {

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler();
        server.setHandler(context);

        AirportServer airportServer = new AirportServer();
        context.setAttribute("airportServer", airportServer);

        // Servlets registration
        context.addServlet(new ServletHolder(new StartAirportServlet()), "/airport/start");
        context.addServlet(new ServletHolder(new PauseAirportServlet()), "/airport/pause");
        context.addServlet(new ServletHolder(new ResumeAirportServlet()), "/airport/resume");
        context.addServlet(new ServletHolder(new StopAirportServlet()), "/airport/stop");

        try {
            server.start();
            System.out.println("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
