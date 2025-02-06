package unit_tests.api;

import com.jakub.bone.grpc.AirportProto;
import com.jakub.bone.grpc.AirportServiceGrpc;
import com.jakub.bone.rpc.AirportServiceImpl;
import com.jakub.bone.server.AirportServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RpcMonitoringTest {
    static Server server;
    static ManagedChannel channel;
    @Mock
    static AirportServer airportServer;
    @InjectMocks
    static AirportServiceImpl airportService;
    static AirportServiceGrpc.AirportServiceBlockingStub stub;

    @BeforeAll
    static void setUp() throws IOException, SQLException {
        airportService = new AirportServiceImpl();

        server = ServerBuilder
                .forPort(8080)
                .addService(airportService).build();
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
        when(airportServer.getUptime()).thenReturn(Duration.ofHours(2));

        AirportProto.UptimeResponse response = stub.getUptime(AirportProto.UptimeRequest.newBuilder().build());

        assertEquals("02:00:00", response.getUptime());
    }

    @Test
    @DisplayName("Should test PlanesCountRequest response correctness")
    void testPlanesCountRequest() throws SQLException {
        when(airportService.getAirportServer().getControlTowerService().getPlanes().size()).thenReturn(5);

        AirportProto.PlanesCountResponse response = stub.getPlanesCount(AirportProto.PlanesCountRequest.newBuilder().build());

        assertEquals(5, response.getCount());
    }
}
