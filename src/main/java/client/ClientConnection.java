package client;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

import java.net.Socket;

@Log4j2
@Getter
public class ClientConnection {
    private final int PORT_NUMBER = 5000;
    private Socket clientSocket;
    public static int connectionAttempts = 0;

    public ClientConnection() {
        connectToServer();
    }

    private void connectToServer()  {
        try {
            clientSocket = new Socket("localhost", PORT_NUMBER);
            log.info("Connection established with server at port {}", PORT_NUMBER);
        } catch (IOException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", PORT_NUMBER, ex.getMessage());
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
            connectToServer();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Reconnection attempt interrupted: {} ", ie.getMessage());
        }
    }

    private void close(){
        try{
            if(clientSocket != null && !clientSocket.isClosed()){
                clientSocket.close();
            }
            log.info("Client disconnected");
        } catch (IOException ex){
            log.error("Error occurred while disconnection: {}", ex.getMessage());
        }
    }
}
