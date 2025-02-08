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

class RpcControlTest {
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
    @DisplayName("Should test StartRequest response correctness")
    void testStartRequest() {
        AirportProto.StatusResponse response = stub.start(AirportProto.StartRequest.newBuilder().build());
        assertEquals("airport started successfully", response.getMessage());
    }

    @Test
    @DisplayName("Should test StopRequest response correctness")
    void testStopRequest() {
        AirportProto.StatusResponse response = stub.start(AirportProto.StartRequest.newBuilder().build());
        assertEquals("airport started successfully", response.getMessage());

        AirportProto.StatusResponse response2 = stub.stop(AirportProto.StopRequest.newBuilder().build());
        assertEquals("airport stopped successfully", response2.getMessage());
    }

    @Test
    @DisplayName("Should test PauseRequest response correctness")
    void testPauseRequest() {
        AirportProto.StatusResponse response = stub.pause(AirportProto.PauseRequest.newBuilder().build());
        assertEquals("airport paused successfully", response.getMessage());
    }

    @Test
    @DisplayName("Should test ResumeRequest response correctness")
    void testResumeRequest() {
        AirportProto.StatusResponse response = stub.resume(AirportProto.ResumeRequest.newBuilder().build());
        assertEquals("airport resumed successfully", response.getMessage());
    }

    /*@Test
    @DisplayName("Should test GetLandedPlanes response correctness")
    void testGetLandedPlanes() {
        AirportProto.LandedPlanesResponse response = stub.getLandedPlanes(AirportProto.LandedPlanesRequest.newBuilder().build());
        assertEquals("no planes collided", response.getLandedPlanes());
    }*/

    @Test
    @DisplayName("Should test GetCollidedPlanes response correctness")
    void testGetCollidedPlanes() {
        AirportProto.CollidedPlanesResponse response = stub.getCollidedPlanes(AirportProto.CollidedPlanesRequest.newBuilder().build());
        assertEquals("no planes collided", response.getCollidedPlanes());
    }

    @Test
    @DisplayName("Should test GetPlaneRequest response correctness")
    void testGetPlaneRequest() {
        AirportProto.PlaneRequest request = AirportProto.PlaneRequest.newBuilder().setFlightNumber("ABC123").build();
        AirportProto.PlaneResponse response = stub.getPlaneRequest(request);
        assertEquals("ABC123", response.getFlightNumber());
        assertEquals("not found", response.getPhase());
        assertEquals("unknown", response.getLocation());
        assertEquals("0", response.getFuelLevel());
    }
}
