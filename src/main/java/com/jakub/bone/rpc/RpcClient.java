package com.jakub.bone.rpc;

import com.jakub.bone.grpc.AirportProto;
import com.jakub.bone.grpc.AirportServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class RpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        AirportServiceGrpc.AirportServiceBlockingStub stub
                = AirportServiceGrpc.newBlockingStub(channel);

        AirportProto.LandedPlanesResponse response = stub.getLandedPlanes(AirportProto.LandedPlanesRequest.newBuilder().build());
        System.out.println("Response: " + response.getLandedPlanes());
    }
}
