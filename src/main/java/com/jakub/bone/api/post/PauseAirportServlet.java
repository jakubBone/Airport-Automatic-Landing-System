package com.jakub.bone.api.post;

import com.google.gson.Gson;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
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
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    private Lock lock = new ReentrantLock();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean paused = false;
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        lock.lock();
        try {
            if (airportServer != null && airportServer.isPaused()) {
                paused = false;
            } else {
                airportServer.pauseServer();
                paused = true;
            }
        } finally {
            lock.unlock();
        }

        if (paused) {
            messenger.send(response, "Airport paused successfully");
        } else {
            messenger.send(response, "Airport is already paused");
        }
    }
}
