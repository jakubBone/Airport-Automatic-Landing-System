package com.jakub.bone.api.get;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.utills.Messenger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jooq.DSLContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static jooq.Tables.PLANES;

@WebServlet(urlPatterns = "/airport/planes/*")
public class PlanesAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    private DSLContext context;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.airportServer = (AirportServer) getServletContext().getAttribute("airportServer");
        this.context = airportServer.getDatabase().getCONTEXT();

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
            case "/landed":
                messenger.send(response, getLandedPlanes());
                break;
            default:
                Plane plane = findPlaneByNumber(planes, path)
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

    public List<String> getLandedPlanes(){
        return context.select(PLANES.FLIGHT_NUMBER)
                .from(PLANES)
                .where(PLANES.LANDING_TIME.isNotNull())
                .fetchInto(String.class);
    }

    public Plane findPlaneByNumber(List<Plane> planes, String path){
        String flightNumber = path.substring(1);
        return planes.stream()
                .filter(p -> p.getFlightNumber().equals(flightNumber))
                .findFirst()
                .orElse(null);
    }
}
