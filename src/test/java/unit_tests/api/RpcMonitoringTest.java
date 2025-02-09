package unit_tests.api;

import com.jakub.bone.grpc.AirportProto;
import com.jakub.bone.grpc.AirportServiceGrpc;
import com.jakub.bone.rpc.AirportServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RpcMonitoringTest {
    static Server server;
    static ManagedChannel channel;
    static AirportServiceGrpc.AirportServiceBlockingStub stub;
    @BeforeAll
    static void setUp() throws IOException, SQLException {
        server = ServerBuilder
                .forPort(8080)
                .addService(new AirportServiceImpl()).build();
        server.start();

        channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        stub = AirportServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    static void tearDown() {
        server.shutdownNow();
        channel.shutdownNow();
    }

    @Test
    @DisplayName("Should test UptimeRequest response correctness")
    void testUpdateRequest() throws SQLException {
        AirportProto.UptimeResponse response = stub.getUptime(AirportProto.UptimeRequest.newBuilder().build());

        assertEquals("airport is not running", response.getUptime());
    }

    @Test
    @DisplayName("Should test PlanesCountRequest response correctness")
    void testPlanesCountRequest() throws SQLException {
        AirportProto.PlanesCountResponse response = stub.getPlanesCount(AirportProto.PlanesCountRequest.newBuilder().build());
        assertEquals("airspace is empty", response.getCount());
    }

    @Test
    @DisplayName("Should test FlightNumbersResponse response correctness")
    void testFlightNumbersResponse() throws SQLException {
        AirportProto.FlightNumbersResponse response = stub.getFlightNumbers(AirportProto.FlightNumbersRequest.newBuilder().build());
        assertEquals("airspace is empty", response.getFlightNumbers());
    }

    @Test
    @DisplayName("Should test GetLandedPlanes response correctness")
    void testGetLandedPlanes() {
        AirportProto.LandedPlanesResponse response = stub.getLandedPlanes(AirportProto.LandedPlanesRequest.newBuilder().build());
        assertEquals("no planes landed", response.getLandedPlanes());
    }

    @Test
    @DisplayName("Should test GetCollidedPlanes response correctness")
    void testGetCollidedPlanes() {
        AirportProto.CollidedPlanesResponse response = stub.getCollidedPlanes(AirportProto.CollidedPlanesRequest.newBuilder().build());
        assertEquals("no planes collided", response.getCollidedPlanes());
    }

    @Test
    @DisplayName("Should test GetPlaneRequest response correctness")
    void testGetPlaneRequest() {
        AirportProto.PlaneRequest request = AirportProto.PlaneRequest.newBuilder().setFlightNumber("AB123").build();
        AirportProto.PlaneResponse response = stub.getPlaneRequest(request);
        assertEquals("AB123", response.getFlightNumber());
        assertEquals("not found", response.getPhase());
        assertEquals("unknown", response.getLocation());
        assertEquals("0", response.getFuelLevel());
    }
}
