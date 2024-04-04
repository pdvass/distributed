package distributed;

import distributed.Estate.*;
import distributed.Share.*;

import java.net.*;

import java.io.*;
import java.io.Serializable;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

/** The Worker class mainly needed for keeping information about the Room objects which
 * correspond to each worker.
 * 
 * @author panagou
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

    public void startWorker() throws IOException {
        Socket socket = new Socket("localhost",4555);

        this.rec = new Request(socket,"worker connection");
        this.rec.sendMessage();

        System.out.println(rec.receiveMessage());

        if(!(this.isAlive && this.rec == null)) {
            this.isAlive = true ;
            runWorker();
        }
    }

    public void runWorker() throws IOException{
        
        ArrayList<Rooms> filteredRooms = new ArrayList<>();
        ArrayList<Room> RequestedRooms = new ArraList<>();

        while (this.isAlive) {
            // System.out.println("Worker is working...");
            String msg = this.rec.receiveMessage();
            try {
                System.out.println(msg);
                if (msg.equals("filters")){
                    filteredRooms = rooms.applyFilter();

                } else if (msg.equals("get hotels")){
                    RequestedRooms = rooms.returnRooms();
                } else if (msg.equals("booking")){

                }
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<Integer, Room> returnRooms() {
        ArrayList<Room> requestedRooms = new ArraList<>();
        for(Room room: this.rooms.values()) {
            requestedRooms.add(room);
        }

        return requestedRooms;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

        public void stop() throws IOException{
        this.isAlive = false ;
    }
}
