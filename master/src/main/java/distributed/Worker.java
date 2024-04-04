package distributed;

import distributed.Estate.*;
import distributed.Share.*;

import java.net.*;

import java.io.*;
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

public class Worker {

    private String name;
    private boolean isAlive;
    private Map<Integer, Room> rooms = new HashMap<Integer, Room>();
    private Request rec = null;

    public Worker(String workerName) {
        this.name = workerName;
        this.isAlive = false;
    }

    public void startWorker( ) throws IOException {
        Socket socket = new Socket("localhost", 4555);

        this.rec = new Request(socket, "worker connection");
        this.rec.sendMessage();

        System.out.println(rec.receiveMessage());

        if(!(this.isAlive && this.rec == null)) {
            this.isAlive = true;
            runWorker();
        }
    }

    public void runWorker() throws IOException {

    }

    public String getName() {
        return this.name;
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

    public boolean isAlive() {
        return isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void sendData(Object data) {
        System.out.println(this.name + " is sending data: " + data);
    }

    public void stop() throws IOException {
        this.isAlive = false;
    }

}