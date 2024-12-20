package integration_tests;


import client.PlaneClient;
import controller.ControlTower;
import database.AirportDatabase;
import org.junit.jupiter.api.*;
import server.AirportServer;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientServerConnectionIntegrationTest {
    AirportDatabase database;
    ControlTower controlTower;
    AirportServer server;

    @BeforeEach
    void setUp()throws IOException, SQLException {
        this.database = new AirportDatabase();
        this.controlTower = new ControlTower(database);
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
    @DisplayName("Should test client-server connection")
    void testClientServerConnection() {
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
}
