package com.jakub.bone.client;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Log4j2
@Getter
public class Client {
    private Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private int port;
    private String ip;
    protected boolean isConnected;

    public Client(String ip,int port) {
        this.ip = ip;
        this.port = port;
    }

    protected void startConnection() {
        try {
            this.socket = new Socket(ip, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.isConnected = true;
            log.debug("Connection established successfully");
        } catch (IOException ex) {
            log.error("Failed to connect to server at {}:{} - {}", ip, port, ex.getMessage());
        }
    }

    protected void stopConnection() {
        closeResources(out, in);
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ex) {
                log.error("Failed to close socket: {}", ex.getMessage(), ex);
            }
        }
    }

    private void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ex) {
                    log.error("Failed to close resource: {}", ex.getMessage(), ex);
                }
            }
        }
    }

    /*protected void stopConnection() {
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
    }*/
}
