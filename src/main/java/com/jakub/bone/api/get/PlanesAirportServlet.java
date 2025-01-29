package com.jakub.bone.api.get;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/airport/planes/*")
public class PlanesAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private static Messenger messenger = new Messenger();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        List<Plane> planes = airportServer.getControlTower().getPlanes();
        List<String> flightNumbers = new ArrayList<>();

        for(Plane plane: planes){
            flightNumbers.add(plane.getFlightNumber());
        }

        String path = request.getPathInfo();
        switch(path) {
            case "/count":
                messenger.send(response, planes.size());
                break;
            case "/flightNumbers":
                messenger.send(response, flightNumbers);
                break;
            default:
                String flightNumber = path.substring(1);
                Plane plane = planes.stream()
                        .filter(p -> p.getFlightNumber().equals(flightNumber))
                        .findFirst()
                        .orElse(null);

                if (plane == null) {
                    messenger.send(response, "Plane not found");
                } else {
                    messenger.send(response,
                            "flightNumber: " + plane.getFlightNumber() +
                                    " status: " + plane.getPhase().toString() +
                                    " altitude: " + plane.getNavigator().getLocation().getAltitude() +
                                    " fuel: " + plane.getFuelManager().getFuelLevel());
                }
        }
    }
}
