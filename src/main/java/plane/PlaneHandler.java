package plane;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Log4j2
public class PlaneHandler implements Runnable {
    private Socket socket;
    private static final int MAX_PLANES = 100;
    private static int landingId;
    private static ArrayList<Plane> planes;
    private static Queue <Runway> availableRunways;
    private static final Lock lock = new ReentrantLock();

    public PlaneHandler(Socket socket) {
        this.socket = socket;
        this.planes = new ArrayList<>();
        this.availableRunways = new LinkedList<>();
    }

    @Override
    public void run() {
        try(PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            String request;
            while ((request = in.readLine()) != null) {
                log.info("Request received: " + request);

                lock.lock();
                try{
                    log.info("Check number of planes in the queue");
                    if(planes.size() >= MAX_PLANES){
                        out.println("Airport is full. Wait");
                        socket.close();
                        return;
                    }

                    log.info("Add plane to queue");
                    int landingID = getLandingID();
                    Plane plane = new Plane(landingID);
                    planes.add(plane);

                    log.info("Check the runways availability");
                    if(availableRunways.isEmpty()){
                       out.println("No runways available. Wait for landing");
                       waitForRunway();
                    }

                    log.info("Poll and assign the runway");
                    Runway assignedRunway = availableRunways.poll();
                    out.println("Assigned to runway: " + assignedRunway.getId());

                    log.info("Land and return the runway");
                    assignedRunway.landPlane(plane);
                    availableRunways.add(assignedRunway);

                    log.info("Remove the plane from queue");
                    planes.remove(plane);
                    out.println("Welcome in Mexico");
                } finally {
                    lock.unlock();
                }
            }
        } catch(IOException ex) {
            log.error("Error occurred while handling client request: {}", ex.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ex) {
                log.error("Error occurred while closing client socket: {}", ex.getMessage());
            }
        }
    }

    public void waitForRunway() {
        while(availableRunways.isEmpty()){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ex){
                log.error("Error occurred while waiting for landing: {}", ex.getMessage());
            }
        }
    }
    public int getLandingID(){
        landingId++;
        return landingId;
    }
}
