package client;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log4j2
public class Client {
    private ClientConnection connection;
    private PrintWriter outToServer;
    private BufferedReader inFromServer;
    private final Socket socket;

    public Client() throws IOException {
        this.connection = new ClientConnection();
        this.socket = connection.getClientSocket();
        this.outToServer = new PrintWriter(socket.getOutputStream(), true);
        this.inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            if (outToServer != null) {
                outToServer.close();
            }
            if (inFromServer != null) {
                inFromServer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing resources: {}", ex.getMessage());
        }
    }
}
