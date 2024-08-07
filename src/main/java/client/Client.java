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
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private static int connectionAttempts = 0;

    private void startConnection(String ip, int port)  {
        try {
            clientSocket = new Socket(ip, port);
            this.outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            log.info("Connection established with server at port {}", port);
        } catch (IOException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", port, ex.getMessage());
            retryConnection();
        }
    }

    private void retryConnection() {
        if (connectionAttempts >= 2) {
            log.error("Max reconnection attempts reached. Giving up");
            close();
            return;
        }
        try {
            Thread.sleep(2000);
            log.info("Attempting to reconnect to the server... (Attempt {})", connectionAttempts + 1);
            connectionAttempts++;
            startConnection();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Reconnection attempt interrupted: {} ", ie.getMessage());
        }
    }

    private void sendRequest(String message){
        try {
            if (outToServer != null) {
                outToServer.println(message);
            }
        } catch (IOException ex) {
            log.error("Connection to server is not established");
        }
    }

    public String readResponse() {
        try {
            if (inFromServer != null) {
                return inFromServer.readLine();
            }
        } catch (IOException ex) {
            log.error("Error occurred while communicating with server: {}", ex.getMessage());
        }
        return null;
    }

    public void close() {
        try {
            if(clientSocket != null && !clientSocket.isClosed()){
                clientSocket.close();
            }
            if (outToServer != null) {
                outToServer.close();
            }
            if (inFromServer != null) {
                inFromServer.close();
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing resources: {}", ex.getMessage());
        }
    }
}
