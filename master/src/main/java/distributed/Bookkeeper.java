package distributed;

import distributed.Estate.*;
import distributed.JSONFileSystem.JSONDirManager;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * The Bookkeeper class generally holds the information that exists on workers. Essentially 
 * it should remember the rooms that each worker has, and check if the workers are "alive".
 * 
 * @see Worker
 * @see Hotel
 * 
 * @author panagou
 */

public class Bookkeeper {

    private static volatile Map<String, Worker> workers = new HashMap<String, Worker>();
    private static volatile ArrayList<Hotel> hotels = new ArrayList<>();
    private static volatile ArrayList<String> users = new ArrayList<>();

    public Bookkeeper() {}

    public Map<String, Worker> getWorkers() {
        return workers;
    }

    public Worker getWorker(String workerHashCode) {
        return workers.get(workerHashCode);
    }

    public void addWorker() {
        String workerName = Integer.toString(workers.size());
        Worker worker = new Worker(workerName);

        String workerHashCode = Integer.toString(worker.hashCode());
        workers.put(workerHashCode, worker);
    }

    public void removeWorker(String workerHashCode) {
        workers.remove(workerHashCode);
    }

    public void addUser() {
        String user = "User" + Integer.toString(users.size());
        users.add(user);
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void removeUser() {
        users.removeLast();
    }

    public ArrayList<Hotel> getHotels(JSONDirManager manager) throws FileNotFoundException, Exception {
        setHotels(manager.getHotels());
        return manager.getHotels();
    }

    public ArrayList<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(ArrayList<Hotel> listOfHotels) {
        hotels = listOfHotels;
    }

    public ArrayList<Room> getRooms(Hotel hotel) {
        return hotel.getRooms();
    }

    public ArrayList<Room> getAllRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        for (Worker worker: workers.values()) {
            rooms.addAll(worker.returnRooms());
        }

        return rooms;
    }

    public void addRoom(String workerHashCode, int roomId, Room room) {
        (workers.get(workerHashCode)).addRoom(roomId, room);
    }

    public void removeRoom(String workerHashCode, int roomId) {
        (workers.get(workerHashCode)).removeRoom(roomId);
    }

    /** 
     * This method reads the number of workers from an env file and creates the workers objects. 
     * It also creates a thread for each worker, which checks if the workers are "alive". 
    **/
    public void checkWorkers() {

        Dotenv dotenv = Dotenv.load();
        String workersEnv = dotenv.get("WORKERS");

        System.out.println(workersEnv);

        int nOfWorkers = Integer.parseInt(workersEnv);

        for (int i=1; i<=nOfWorkers; i++) {
            addWorker();
        }

        ExecutorService executor = Executors.newFixedThreadPool(nOfWorkers); // One Thread per Worker

        // Thread for checking if workers are alive
        while (true) {
            for (int j=1; j<=nOfWorkers; j++) {
                Worker requestedWorker = workers.get("worker"+j);
    
                    if (requestedWorker.isAlive()) {
                        executor.submit(() -> {
                            System.out.println("The workers with " + requestedWorker.hashCode() + " hashCode is alive.");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        System.out.println("The worker with the name " +requestedWorker.hashCode()+ " is not alive");

                        String workerHashCode = Integer.toString(requestedWorker.hashCode());
                        removeWorker(workerHashCode);
                
                        nOfWorkers--;
                            
                        try (FileWriter writer = new FileWriter(".env")) {
                            writer.write("WORKERS=" + nOfWorkers);
                            dotenv = Dotenv.configure().directory(".").load();
                        } catch (IOException e) {
                            System.out.println("An error occurred while updating .env file: " + e.getMessage());
                        }
                        this.distributingRooms(requestedWorker);
                    }
            }
        }

    }

    /** 
     * This method creates a thread pool of one thread to send a message to a Worker. 
     * @param worker representing the Worker object to which we want to send.
    **/
    public void sendData(Worker worker) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(() -> {
            worker.sendData("Data to Worker" + worker.hashCode());
        });

        
    }

    /**
     * This method is used, in case a worker "dies", it will distribute its data to the rest of the workers.
     * @param worker representing the Worker object which has been lost.
     **/
    public void distributingRooms(Worker worker) {
        ArrayList<Room> rooms = worker.returnRooms();
        int numberOfActiveWorkers = workers.size();

        for (Room room: rooms) {
            int roomId = room.getIntId();
            int workerIndex = roomId % numberOfActiveWorkers;  // Calculation of the worker's index

            String targetWorkerName = "Worker " + workerIndex; 

            addRoom(targetWorkerName, roomId, room);
            System.out.println("Room with hash " + roomId + " distributed to " + targetWorkerName);
        }
    }

}