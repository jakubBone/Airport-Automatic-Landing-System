package com.jakub.bone.api;

import org.eclipse.jetty.server.Server;

public class ApiServer {
    public static void main(String[] args) {
        Server server = new Server(8080);

        try {
            server.start();
            System.out.println("Server is running on http://localhost:8080");
            try {
                server.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
