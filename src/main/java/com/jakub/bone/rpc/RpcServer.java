package com.jakub.bone.rpc;

import com.jakub.bone.api.control.PauseAirportServlet;
import com.jakub.bone.api.control.ResumeAirportServlet;
import com.jakub.bone.api.control.StartAirportServlet;
import com.jakub.bone.api.control.StopAirportServlet;
import com.jakub.bone.api.monitoring.CollisionsAirportServlet;
import com.jakub.bone.api.monitoring.PlanesAirportServlet;
import com.jakub.bone.api.monitoring.UptimeAirportServlet;
import com.jakub.bone.server.AirportServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.sql.SQLException;

public class RpcServer {


    public static void main(String[] args) throws SQLException {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        server.setHandler(context);

        AirportServer airportServer = new AirportServer();
        context.setAttribute("airportServer", airportServer);

        try {
            server.start();
            System.out.println("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("API Server interrupted: " + ex.getMessage());
            }
        } catch (Exception ex) {
            System.err.println("Failed to start API Server: " + ex.getMessage());
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                System.err.println("Failed to stop API Server: " + e.getMessage());
            }
        }
    }

    // Init Servlets
        /*context.addServlet(new ServletHolder(new StartAirportServlet()), "/airport/start");
        context.addServlet(new ServletHolder(new PauseAirportServlet()), "/airport/pause");
        context.addServlet(new ServletHolder(new ResumeAirportServlet()), "/airport/resume");
        context.addServlet(new ServletHolder(new StopAirportServlet()), "/airport/stop");
        context.addServlet(new ServletHolder(new UptimeAirportServlet()), "/airport/uptime");
        context.addServlet(new ServletHolder(new PlanesAirportServlet()), "/airport/planes/*");
        context.addServlet(new ServletHolder(new CollisionsAirportServlet()), "/airport/collisions");*/

}
