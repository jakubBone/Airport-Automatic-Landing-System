package client;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
public class Client {
    private int connectionAttempts = 0;
    protected Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private int port;
    private String ip;
    private boolean stopReconnection = false;
    protected boolean isConnected;

    public Client(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    protected void startConnection() {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
        } catch (IOException ex) {
            if(stopReconnection){
                return;
            }
            log.error("Failed to establish connection with the server at port {}. Error: {}", port, ex.getMessage());
        }
    }

    protected void stopConnection() {
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
}
