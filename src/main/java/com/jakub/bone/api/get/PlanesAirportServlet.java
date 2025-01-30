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
import java.util.*;

import static jooq.Tables.PLANES;

@WebServlet(urlPatterns = "/airport/planes/*")
public class PlanesAirportServlet extends HttpServlet {
    private AirportServer airportServer;
    private Messenger messenger = new Messenger();
    private DSLContext context;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{
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
                    messenger.send(response, Map.of("count", planes.size()));
                    break;
                case "/flightNumbers":
                    messenger.send(response, Map.of("flight numbers", flightNumbers));
                    break;
                case "/landed":
                    messenger.send(response, Map.of("landed planes", getLandedPlanes()));
                    break;
                default:
                    Plane plane = findPlaneByNumber(planes, path);
                    if (plane == null) {
                        messenger.send(response, Map.of("message" ,"plane not found"));
                    } else {
                        messenger.send(response, mapPlane(plane));
                    }
            }
        } catch (Exception ex){
            messenger.send(response, Map.of("error", "Internal server error"));
            System.err.println("Error handling request: " + ex.getMessage());
        }
    }

    private List<String> getLandedPlanes(){
        return context.select(PLANES.FLIGHT_NUMBER)
                .from(PLANES)
                .where(PLANES.LANDING_TIME.isNotNull())
                .fetchInto(String.class);
    }

    private Plane findPlaneByNumber(List<Plane> planes, String path){
        String flightNumber = path.substring(1);
        return planes.stream()
                .filter(p -> p.getFlightNumber().equals(flightNumber))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> mapPlane(Plane plane){
        Map<String, Object> planeMap = new LinkedHashMap<>();
        planeMap.put("flightNumber", plane.getFlightNumber());
        planeMap.put("phase", plane.getPhase());

        Map<String, Object> locationMap = new LinkedHashMap<>();
        locationMap.put("x", plane.getNavigator().getLocation().getX());
        locationMap.put("y", plane.getNavigator().getLocation().getY());
        locationMap.put("altitude", plane.getNavigator().getLocation().getAltitude());

        planeMap.put("location", locationMap);
        planeMap.put("fuel level", plane.getFuelManager().getFuelLevel());

        return planeMap;
    }
}
