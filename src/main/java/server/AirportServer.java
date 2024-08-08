package server;

import lombok.extern.log4j.Log4j2;
import plane.PlaneHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class AirportServer  {
    private static final int NUMBER_OF_THREADS = 100;
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public AirportServer() {
        this.executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    }
    public void startServer(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        executor.submit(new PlaneHandler(clientSocket));
                    }
                } catch (Exception ex) {
                    log.error("Error occurred while accepting client connection: {}", ex.getMessage());
                }
            }
        } catch (IOException ex){
            log.error("Failed to start server on port {}: {}", port, ex.getMessage());
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
        } catch (IOException ex) {
            log.error("Error occurred while closing server socket: {}", ex.getMessage());
        }
    }

    public static void main(String[] args) throws IOException{
        AirportServer airport = new AirportServer();
        airport.startServer(5000);
    }
}
