package integration_tests;


import client.PlaneClient;
import controller.ControlTower;
import database.AirportDatabase;
import database.CollisionDAO;
import database.PlaneDAO;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import plane.Plane;
import server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ClientServerConnectionIntegrationTest {
    @Mock
    AirportDatabase mockDatabase;
    @Mock
    PlaneDAO planeDAO;
    @Mock
    CollisionDAO collisionDAO;
    ControlTower controlTower;
    AirportServer server;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockDatabase.getPLANE_DAO()).thenReturn(planeDAO);
        when(mockDatabase.getCOLLISION_DAO()).thenReturn(collisionDAO);

        this.controlTower = new ControlTower(mockDatabase);
            new Thread(() -> {
                try {
                    this.server = new AirportServer(controlTower);
                    this.server.startServer(5000);
                } catch (IOException | SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }).start();
    }
    @AfterEach
    void tearDown() throws IOException {
        if (server != null) {
            server.stopServer();
        }
    }

    @Test
    @DisplayName("Should test connection with single client")
    void testClientServerConnectionSingleClient() {
        try{
            Thread.sleep(2000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        PlaneClient planeClient = new PlaneClient("localhost", 5000);
        new Thread(planeClient).start();
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }

        assertTrue(planeClient.isConnected(), "Client should successfully connect to the server");
    }

    @Test
    @DisplayName("Should test connection with multiple clients")
    void testConnectionWithMultipleClients() {
        try{
            Thread.sleep(2000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        PlaneClient planeClient1 = new PlaneClient("localhost", 5000);
        new Thread(planeClient1).start();
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }

        PlaneClient planeClient2 = new PlaneClient("localhost", 5000);
        new Thread(planeClient2).start();
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        assertTrue(planeClient1.isConnected(), "Client1 should successfully connect to the server");
        assertTrue(planeClient2.isConnected(), "Client2 should successfully connect to the server");
    }

    @Test
    @DisplayName("Should test client-server registration")
    void testClientRegistration() {
        try{
            Thread.sleep(2000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }
        PlaneClient planeClient = new PlaneClient("localhost", 5000);
        new Thread(planeClient).start();
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        assertEquals(1, server.getControlTower().getPlanes().size(), "Planes list should contain only one Plane object");
    }

    @Test
    @DisplayName("Should test client-server registration with full space capacity")
    void testClientRegistrationWithFullCapacity() {
        try{
            Thread.sleep(2000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        for(int i = 0; i < 100; i++){
            Plane plane = new Plane("TEST_PLANE");
            server.getControlTower().getPlanes().add(plane);
        }

        PlaneClient planeClient = new PlaneClient("localhost", 5000);
        new Thread(planeClient).start();
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ex){
                ex.printStackTrace();
            }
        assertEquals(100, server.getControlTower().getPlanes().size(), "Plane list should contain only 100 planes; new plane not added");
    }
}
