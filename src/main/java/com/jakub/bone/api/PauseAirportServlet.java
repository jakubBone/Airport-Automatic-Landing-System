package com.jakub.bone.api;

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

@WebServlet(urlPatterns = "/airport/pause")
public class PauseAirportServlet extends HttpServlet {
    private static AirportServer airportServer;
    private static Lock lock = new ReentrantLock();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean started = false;
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        try {
            lock.lock();
            if (airportServer != null && airportServer.isPaused()) {
                started = false;
            } else {
                airportServer.setPaused(true);
                started = true;
            }
        } finally {
            lock.unlock();
        }

        String json;
        if (started) {
            json = new Gson().toJson("{\"message\": \"Airport paused successfully\"}");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            json = new Gson().toJson("{\"message\": \"Airport is already paused\"}");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
