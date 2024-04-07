package distributed;

import distributed.Estate.*;
import distributed.Server.Response;

import java.net.*;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * The Worker class mainly needed for keeping information about the Room objects which
 * correspond to each worker.
 * 
 * @author panagou
 * @author stella
 * @see Room
 */

public class Worker extends Thread {

    private String name;
    private Map<Integer, Room> rooms = new HashMap<Integer, Room>();
    private ServerSocket workerSocket = null;
    private final int PORT = 4555;

    public Worker(String workerName) {
        this.name = workerName;
    }

    public void run() {
        try {
            this.init(this.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port) throws IOException {
        this.workerSocket = new ServerSocket(port);
        this.workerSocket.setReuseAddress(true);


        while(true) {
            Socket worker = workerSocket.accept();

            Response res = new Response(worker, null);
    
        }
    }

    public Room getRoom(int roomId) {
        return this.rooms.get(roomId);
    }

    public void addRoom(int roomId, Room room) {
        this.rooms.put(roomId, room);
    }

    public void removeRoom(int roomId) {
        this.rooms.remove(roomId);
    }

    public boolean hasRoom(int roomId) {
        return (this.rooms).containsKey(roomId);
    }

    public ArrayList<Room> returnRooms() {
        ArrayList<Room> requestedRooms = new ArrayList<>();
        for(Room room: this.rooms.values()) {
            requestedRooms.add(room);
        }

        return requestedRooms;
    }

    public void sendData(Object data) {
        System.out.println(this.name + " is sending data: " + data);
    }

    public void close() throws IOException {
        workerSocket.close();
    }

}