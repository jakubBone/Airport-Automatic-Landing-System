package client;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log4j2
public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static int connectionAttempts = 0;

    private void startConnection(String ip, int port)  {
        try {
            clientSocket = new Socket(ip, port);
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            log.info("Connection established with server at port {}", port);
        } catch (IOException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", port, ex.getMessage());
            retryConnection(ip, port);
        }
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

    private void requestLanding() throws IOException{
        startConnection("localhost", 5000);

        sendRequest("The plane request for landing");

        String response = readResponse();
        System.out.println("Airport response: " + response);
    }

    private void sendRequest(String message)  {
        try {
            if (out != null) {
                out.println(message);
            }
        } catch (Exception ex) {
            log.error("Connection to server is not established: {}", ex.getMessage());
        }
    }

    public String readResponse() {
        try {
            if (in != null) {
                return in.readLine();
            }
        } catch (IOException ex) {
            log.error("Error occurred while communicating with server: {}", ex.getMessage());
        }
        return null;
    }

    public void stopConnection() {
        try {
            if(clientSocket != null && !clientSocket.isClosed()){
                clientSocket.close();
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
        Client client = new Client();
        client.requestLanding();
    }

}
