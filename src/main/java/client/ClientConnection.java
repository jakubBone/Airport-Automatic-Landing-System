package client;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log4j2
public class ClientConnection {
    private final int PORT_NUMBER = 5000;
    private Socket clientSocket;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    public static int connectionAttempts = 0;

    public ClientConnection() {
        connectToServer();
    }

    private void connectToServer()  {
        try(Socket socket = new Socket("localhost", PORT_NUMBER)) {
            clientSocket = socket;
            outToServer = new PrintWriter(socket.getOutputStream(), true);
            inFromServer = new BufferedReader(new InputStreamReader(System.out.println()));
            log.info("Connection established with server at port {}", PORT_NUMBER);
        } catch (IOException ex) {
            log.error("Failed to establish connection with the server at port {}. Error: {}", PORT_NUMBER, ex.getMessage());
            retryConnection();
        }
    }

    private void retryConnection() {
        if (connectionAttempts >= 2) {
            log.error("Max reconnection attempts reached. Giving up");
            disconnect();
            return;
        }
        try {
            Thread.sleep(2000);
            log.info("Attempting to reconnect to the server... (Attempt {})", connectionAttempts + 1);
            connectionAttempts++;
            connectToServer();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.warn("Reconnection attempt interrupted", ie);
        }
    }

    private void disconnect(){
        try{
            if(outToServer != null){
                outToServer.close();
            }

            if(inFromServer != null){
                inFromServer.close();
            }

            if(clientSocket != null){
                clientSocket.close();
            }
            log.info("Client disconnected");
        } catch (IOException ex){
            log.error("Error during disconnection: {}", ex.getMessage());
        }
    }
}
