package distributed;

import java.util.ArrayList;
import java.util.HashMap;

public class Bookkeeper {
    private HashMap<String, ArrayList<Room>> workers;
    // private TCPClient;

    public Bookkeeper() {
        this.workers = new HashMap<>();
    }

    public ArrayList<Room> getRooms(String workerName) {
        return workers.get(workerName);
    }

    public void addRoom(String workerName, ArrayList<Room> rooms) {

        ArrayList<Room> existingRooms= workers.get(workerName);
        existingRooms.addAll(rooms);

        workers.put(workerName, existingRooms);
    }

    public void removeRoom(String workerName, String roomName) {
        
        ArrayList<Room> existingRooms= workers.get(workerName);

        if (existingRooms != null) {
            existingRooms.removeIf(room -> room.getId().equals(roomName));
            workers.put(workerName, existingRooms);

        }
    }

    public void sendCommandToWorker(String workerName, String command) {
        // Here you can send specific commands to a single worker
        System.out.println("Sending command to Worker " + workerName + ": " + command);
    }

    public void sendCommandToWorkers(String command) {
        
    }

}