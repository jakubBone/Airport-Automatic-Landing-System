package com.jakub.bone.rpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.sql.SQLException;

public class RpcServer {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(8080)
                .addService(new AirportServiceImpl()).build();

        server.start();
        server.awaitTermination();
    }
}
