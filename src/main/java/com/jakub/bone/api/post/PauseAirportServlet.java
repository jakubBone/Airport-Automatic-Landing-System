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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@WebServlet(urlPatterns = "/airport/pause")
public class PauseAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    private Lock lock = new ReentrantLock();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        lock.lock();
        try {
            if (airportServer != null && airportServer.isPaused()) {
                messenger.send(response, Map.of("message", "airport is already paused"));
            } else {
                airportServer.pauseServer();
                messenger.send(response, Map.of("message", "airport paused successfully"));
            }
        } finally {
            lock.unlock();
        }
    }
}
