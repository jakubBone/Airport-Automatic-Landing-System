package com.jakub.bone.api.get;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import com.jakub.bone.utills.PlaneMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = "/airport/planes/*")
public class PlanesAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger;

    @Override
    public void init() throws ServletException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.messenger = new Messenger();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
            int planesCount= airportServer.getControlTower().getPlanes().size();
            List<String> landedPlanes = airportServer.getDatabase().getPLANE_REPOSITORY().getLandedPlanes();
            List<String> flightNumbers = airportServer.getControlTower().getAllFlightNumbers();

            String path = request.getPathInfo();
            switch(path) {
                case "/count":
                    messenger.send(response, Map.of("count", planesCount));
                    break;
                case "/flightNumbers":
                    messenger.send(response, Map.of("flight numbers", flightNumbers));
                    break;
                case "/landed":
                    messenger.send(response, Map.of("landed planes", landedPlanes));
                    break;
                default:
                    String flightNumber = path.substring(1);
                    Plane plane = airportServer.getControlTower().getPlaneByFlightNumber(flightNumber);
                    if (plane == null) {
                        messenger.send(response, Map.of("message" ,"plane not found"));
                    } else {
                        messenger.send(response, PlaneMapper.mapPlane(plane));
                    }
            }
        } catch (Exception ex){
            messenger.send(response, Map.of("error", "Internal server error"));
            System.err.println("Error handling request: " + ex.getMessage());
        }
    }
}
