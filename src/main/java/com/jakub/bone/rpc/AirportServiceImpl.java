package com.jakub.bone.rpc;

import com.jakub.bone.domain.plane.Plane;
import com.jakub.bone.grpc.AirportProto;
import com.jakub.bone.grpc.AirportServiceGrpc;
import com.jakub.bone.server.AirportServer;
import com.jakub.bone.service.AirportStateService;
import com.jakub.bone.utills.PlaneMapper;
import io.grpc.stub.StreamObserver;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AirportServiceImpl extends AirportServiceGrpc.AirportServiceImplBase {
    private AirportServer airportServer;
    private AirportStateService airportStateService;

    public AirportServiceImpl() throws SQLException {
        this.airportServer = new AirportServer();
        this.airportStateService = new AirportStateService(airportServer);
    }

    @Override
    public void start(AirportProto.StartRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
            airportStateService.startAirport();

            AirportProto.StatusResponse response = AirportProto.StatusResponse.newBuilder()
                            .setMessage("airport started successfully")
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }

    @Override
    public void stop(AirportProto.StopRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        airportStateService.getAirportServer().stopServer();

        AirportProto.StatusResponse response = AirportProto.StatusResponse.newBuilder()
                .setMessage("Airport stopped successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void pause(AirportProto.PauseRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        airportServer.pauseServer();

        AirportProto.StatusResponse response = AirportProto.StatusResponse.newBuilder()
                .setMessage("Airport paused successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void resume(AirportProto.ResumeRequest request, StreamObserver<AirportProto.StatusResponse> responseObserver) {
        airportServer.resumeServer();

        AirportProto.StatusResponse response = AirportProto.StatusResponse.newBuilder()
                .setMessage("Airport paused successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUptime(AirportProto.UptimeRequest request, StreamObserver<AirportProto.UptimeResponse> responseObserver) {
        long hours = airportServer.getUptime().toHours();
        long minutes = airportServer.getUptime().toMinutes();
        long seconds = airportServer.getUptime().getSeconds();

        AirportProto.UptimeResponse response = AirportProto.UptimeResponse.newBuilder()
                .setUptime(String.format("%02d:%02d:%02d", hours, minutes, seconds))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPlanesCount(AirportProto.PlanesCountRequest request, StreamObserver<AirportProto.PlanesCountResponse> responseObserver) {
        int planesCount= airportServer.getControlTowerService().getPlanes().size();

        AirportProto.PlanesCountResponse response = AirportProto.PlanesCountResponse.newBuilder()
                .setCount(planesCount)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getFlightNumbers(AirportProto.FlightNumbersRequest request, StreamObserver<AirportProto.FlightNumbersResponse> responseObserver) {
        List<String> flightNumbers = airportServer.getControlTowerService().getAllFlightNumbers();

        AirportProto.FlightNumbersResponse response = AirportProto.FlightNumbersResponse.newBuilder()
                .setFlightNumbers(String.valueOf(flightNumbers))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getLandedPlanes(AirportProto.LandedPlanesRequest request, StreamObserver<AirportProto.LandedPlanesResponse> responseObserver) {
        List<String> landedPlanes = airportServer.getDatabase().getPLANE_REPOSITORY().getLandedPlanes();
        AirportProto.LandedPlanesResponse response = AirportProto.LandedPlanesResponse.newBuilder()
                .setLandedPlanes(String.valueOf(landedPlanes))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCollidedPlanes(AirportProto.CollidedPlanesRequest request, StreamObserver<AirportProto.CollidedPlanesResponse> responseObserver) {
        List<String> collidedPlanes = airportServer.getDatabase().getCOLLISION_REPOSITORY().getCollidedPlanes();

        AirportProto.CollidedPlanesResponse response = AirportProto.CollidedPlanesResponse.newBuilder()
                .setCollidedPlanes(String.valueOf(collidedPlanes))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPlaneRequest(AirportProto.PlaneRequest request, StreamObserver<AirportProto.PlaneResponse> responseObserver) {
        Plane plane = airportServer.getControlTowerService().getPlaneByFlightNumber(String.valueOf(request));
        Map<String, Object> mappedPlane = PlaneMapper.mapPlane(plane);

        AirportProto.PlaneResponse response = AirportProto.PlaneResponse.newBuilder()
                .setFlightNumber(request.getFlightNumber())
                .setPhase(plane != null ? String.valueOf(mappedPlane.get("phase")) : "NOT FOUND")
                .setLocation(plane != null ? String.valueOf(mappedPlane.get("location")) : "UNKNOWN")
                .setFuelLevel(plane != null ? String.valueOf(mappedPlane.get("fuel level")): "0")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}