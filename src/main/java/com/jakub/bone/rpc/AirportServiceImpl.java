package com.jakub.bone.rpc;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.grpc.AirportProto;
import com.jakub.bone.grpc.AirportServiceGrpc;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.utills.PlaneMapper;
import io.grpc.stub.StreamObserver;
import lombok.Getter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Getter
public class AirportServiceImpl extends AirportServiceGrpc.AirportServiceImplBase {
    private AirportServer airportServer;
    private AirportStateService airportStateService;

    public AirportServiceImpl() throws SQLException {
        this.airportServer = new AirportServer();
        this.airportStateService = new AirportStateService(airportServer);
    }

    @Override
    public void start(AirportProto.StartRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        String message;
        try {
            if (airportServer.isRunning()) {
                message = "airport is already running";
            } else {
                airportStateService.startAirport();
                message = "airport started successfully";
            }
        } catch (Exception ex){
            message = "failed to start airport";
            System.err.println("Error starting airport: " + ex.getMessage());
        }

        sendResponse(responseObserver, buildStatusResponse(message));
    }

    @Override
    public void stop(AirportProto.StopRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        String message;
        try {
            if (!airportServer.isRunning()) {
                message = "airport is not running";
            } else {
                airportStateService.getAirportServer().stopServer();
                message = "airport stopped successfully";
            }
        } catch (Exception ex) {
            message = "failed to stop airport";
            System.err.println("Error stopping airport: " + ex.getMessage());
        }

        sendResponse(responseObserver, buildStatusResponse(message));
    }

    @Override
    public void pause(AirportProto.PauseRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        String message;
        try {
            if (airportServer.isPaused()) {
                message = "airport is already paused";
            } else {
                airportServer.pauseServer();
                message = "airport paused successfully";
            }
        } catch (Exception ex){
            message = "failed to pause airport";
            System.err.println("Error pausing airport: " + ex.getMessage());
        }

        sendResponse(responseObserver, buildStatusResponse(message));
    }

    @Override
    public void resume(AirportProto.ResumeRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        String message = "airport is running";
        try {
            if (airportServer.isPaused()) {
                airportServer.resumeServer();
                message = "airport resumed successfully";
            }
        } catch (Exception ex) {
            message = "failed to resume airport";
            System.err.println("Error resuming airport: " + ex.getMessage());
        }

        sendResponse(responseObserver, buildStatusResponse(message));
    }

    @Override
    public void getUptime(AirportProto.UptimeRequest request, StreamObserver<AirportProto.UptimeResponse> responseObserver) {
        String message;
        try {
            if (airportServer.getStartTime() == null) {
                message = "airport is not running";
            } else {
                long hours = airportServer.getUptime().toHours();
                long minutes = airportServer.getUptime().toMinutes() % 60;
                long seconds = airportServer.getUptime().getSeconds() % 60;
                message = String.format("%02d:%02d:%02d", hours, minutes, seconds);
            }
        } catch (Exception ex){
            message = "failed to retrieve uptime";
            System.err.println("Error retrieving update data: " + ex.getMessage());
        }

        AirportProto.UptimeResponse response = AirportProto.UptimeResponse.newBuilder()
                .setUptime(message)
                .build();
        sendResponse(responseObserver, response);
    }

    @Override
    public void getPlanesCount(AirportProto.PlanesCountRequest request, StreamObserver<AirportProto.PlanesCountResponse> responseObserver) {
        String message = "airspace is empty";
        try {
            int planesCount= airportServer.getControlTowerService().getPlanes().size();
            if (planesCount != 0) {
                message = String.valueOf(planesCount);
            }
        } catch (Exception ex){
            message = "internal server error";
            System.err.println("Error handling request: " + ex.getMessage());
        }

        AirportProto.PlanesCountResponse response = AirportProto.PlanesCountResponse.newBuilder()
                .setCount(message)
                .build();
        sendResponse(responseObserver, response);
    }

    @Override
    public void getFlightNumbers(AirportProto.FlightNumbersRequest request, StreamObserver<AirportProto.FlightNumbersResponse> responseObserver) {
        String message = "airspace is empty";
        try {
            List<String> flightNumbers = airportServer.getControlTowerService().getAllFlightNumbers();
            if (flightNumbers != null) {
                message = String.valueOf(flightNumbers);
            }
        } catch (Exception ex){
            message = "internal server error";
            System.err.println("Error handling request: " + ex.getMessage());
        }

        AirportProto.FlightNumbersResponse response = AirportProto.FlightNumbersResponse.newBuilder()
                .setFlightNumbers(message)
                .build();
        sendResponse(responseObserver, response);
    }

    @Override
    public void getLandedPlanes(AirportProto.LandedPlanesRequest request, StreamObserver<AirportProto.LandedPlanesResponse> responseObserver) {
        String message = "no planes landed";
        try {
            List<String> landedPlanes = airportServer.getDatabase().getPLANE_REPOSITORY().getLandedPlanes();
            if (!landedPlanes.isEmpty()) {
                message = String.valueOf(landedPlanes);
            }
        } catch (Exception ex) {
            message = "internal server error";
            message = ex.getMessage();
            System.err.println("Error handling request: " + ex.getMessage());
        }
        AirportProto.LandedPlanesResponse response = AirportProto.LandedPlanesResponse.newBuilder()
                .setLandedPlanes(message)
                .build();
        sendResponse(responseObserver, response);
    }

    @Override
    public void getCollidedPlanes(AirportProto.CollidedPlanesRequest request, StreamObserver<AirportProto.CollidedPlanesResponse> responseObserver) {
        String message = "no planes collided";
        try {
            List<String> collidedPlanes = airportServer.getDatabase().getCOLLISION_REPOSITORY().getCollidedPlanes();
            if (!collidedPlanes.isEmpty()) {
                message = String.valueOf(collidedPlanes);
            }
        } catch (Exception ex) {
            message = "internal server error";
            System.err.println("Error handling request: " + ex.getMessage());
        }

        AirportProto.CollidedPlanesResponse response = AirportProto.CollidedPlanesResponse.newBuilder()
                .setCollidedPlanes(String.valueOf(message))
                .build();
        sendResponse(responseObserver, response);
    }

    @Override
    public void getPlaneRequest(AirportProto.PlaneRequest request, StreamObserver<AirportProto.PlaneResponse> responseObserver) {
        String phase = "not found";
        String location = "unknown";
        String fuelLevel = "0";
        AirportProto.PlaneResponse response;

        try {
            Plane plane = airportServer.getControlTowerService().getPlaneByFlightNumber(String.valueOf(request));
            Map<String, Object> mappedPlane = PlaneMapper.mapPlane(plane);

            phase = String.valueOf(mappedPlane.get("phase"));
            location = String.valueOf(mappedPlane.get("location"));
            fuelLevel = String.valueOf(mappedPlane.get("fuel level"));

        } catch (Exception ex) {
            System.err.println("Error handling request: " + ex.getMessage());
        }

        response = AirportProto.PlaneResponse.newBuilder()
                .setFlightNumber(request.getFlightNumber())
                .setPhase(phase)
                .setLocation(location)
                .setFuelLevel(fuelLevel)
                .build();
        sendResponse(responseObserver, response);
    }

    // Helper methods to build and send responses
    private <T> void sendResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private AirportProto.StatusResponse buildStatusResponse(String message){
        return AirportProto.StatusResponse.newBuilder().setMessage(message).build();
    }
}