package plane;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.Socket;

@Log4j2
public class PlaneClient {
    private static int connectionAttempts = 0;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private int port;
    private String ip;
    private Plane plane;
    public PlaneClient(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    private void startConnection()  {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream();
            in = new ObjectInputStream(socket.getInputStream()

            out.writeObject(plane);

            while(true){
                //updatePlaneState();

                Plane updatedPlane = (Plane) in.readObject();

                processAirportInstruction(updatedPlane);

                if(plane.isLanded() || plane.getFuelLevel() <= 0){
                    break;
                }

                Thread.sleep(1000);
            }
            retryConnection(ip, port);
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", port, ex.getMessage());
        }
    }

    public void updatePlaneState(){
        plane.updateLocation();
        plane.reduceFuel();

    }
    public void processAirportInstruction(Plane updatedPlane){
        // set the plane's state based on the airport's instructions
        plane.setCurrentLocation(updatedPlane.getCurrentLocation());
        plane.setHeading(updatedPlane.getHeading());
        plane.getSpeed(updatedPlane.getSpeed());
    }

    private void retryConnection(String ip, int port) {
        if (connectionAttempts >= 2) {
            log.error("Max reconnection attempts reached. Giving up");
            stopConnection();
            return;
        }
        try {
            Thread.sleep(2000);
            log.info("Attempting to reconnect to the server... (Attempt {})", connectionAttempts + 1);
            connectionAttempts++;
            startConnection(ip, port);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Reconnection attempt interrupted: {} ", ie.getMessage());
        }
    }

    public void stopConnection() {
        try {
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing resources: {}", ex.getMessage());
        }
    }
    public static void main(String[] args) throws IOException {
        PlaneClient client = new PlaneClient("localhost", 5000);
        client.startConnection();
    }

    /*private void startConnection(String ip, int port)  {
        try {
            clientSocket = new Socket(ip, port);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            log.info("Connection established with server at port {}", port);
        } catch (IOException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", port, ex.getMessage());
            retryConnection(ip, port);
        }
    }*/
}
