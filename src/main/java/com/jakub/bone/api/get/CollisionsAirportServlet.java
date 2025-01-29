package com.jakub.bone.api.get;

import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jooq.DSLContext;

import java.io.IOException;
import java.util.List;

import static jooq.Tables.COLLISIONS;
@WebServlet(urlPatterns = "/airport/collisions")
public class CollisionsAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    private DSLContext context;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.context = airportServer.getDatabase().getCONTEXT();

        messenger.send(response, getCollidedPlanes());
    }

    public List<String> getCollidedPlanes(){
        return context.select(COLLISIONS.INVOLVED_PLANES)
                .from(COLLISIONS)
                .where(COLLISIONS.TIME.isNotNull())
                .fetchInto(String.class);
    }
}
