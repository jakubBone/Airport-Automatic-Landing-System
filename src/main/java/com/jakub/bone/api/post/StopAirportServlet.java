package com.jakub.bone.api.get;

import com.google.gson.Gson;
import com.jakub.bone.server.AirportServer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@WebServlet(urlPatterns = "/airport/stop")
public class StopAirportServlet extends HttpServlet {
    private static AirportServer airportServer;
    private static Lock lock = new ReentrantLock();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        lock.lock();
        try {
            if (airportServer != null && airportServer.isRunning()) {
                airportServer.stopServer();
            }
        } finally {
            lock.unlock();
        }

        String json = new Gson().toJson("{\"message\": \"Airport stopped successfully\"}");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
