package distributed;

import distributed.Estate.*;
import distributed.JSONFileSystem.JSONDirManager;
import distributed.Share.Mail;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.net.UnknownHostException;
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

    private static volatile Map<String, ArrayList<Room>> workers = new HashMap<>();
    private Map<String, Process> workerProcesses = new ConcurrentHashMap<>();
    private static volatile ArrayList<Hotel> hotels = new ArrayList<>();
    private static volatile ArrayList<String> users = new ArrayList<>();

    public Bookkeeper() {}

    public Map<String, ArrayList<Room>> getWorkers() {
        return workers;
    }

    public void addWorker() {
        String workerName = "Worker" + workers.size();
        workers.put(workerName, null);

    }

    public void removeWorker(String workerName) {
        workers.remove(workerName);
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

    public ArrayList<Hotel> getHotels() throws FileNotFoundException,Exception {
        JSONDirManager manager = new JSONDirManager();
        hotels = manager.getHotels();
        return hotels;
    }

    public ArrayList<Room> getRooms(String workerName) {
        return workers.get(workerName);
    }

    public ArrayList<Room> getAllRooms() throws FileNotFoundException,Exception {
        ArrayList<Hotel> hotels = this.getHotels();
        ArrayList<Room> rooms = new ArrayList<>();
        for (Hotel hotel: hotels) {
            rooms.addAll(hotel.getRooms());
        }

        return rooms;
    }    

    /** 
     * This method reads the number of workers from an env file and creates the workers objects. 
     * It also creates a thread for each worker, which checks if the workers are "alive". 
     * 
     * @throws FileNotFoundException
     * @throws Exception
    **/
    public void checkWorkers() throws FileNotFoundException, Exception {

        Dotenv dotenv = Dotenv.load();
        String workersEnv = dotenv.get("WORKERS");

        System.out.println(workersEnv);

        int nOfWorkers = Integer.parseInt(workersEnv);

        this.createWorkers();

        // ExecutorService executor = Executors.newFixedThreadPool(nOfWorkers); // One Thread per Worker

        // // Thread for checking if workers are alive
        // while (true) {
        //     for (int j=0; j<=nOfWorkers; j++) {
        //         Worker requestedWorker = workers.get("worker"+j);
    
        //             if (requestedWorker.isAlive()) {
        //                 executor.submit(() -> {
        //                     System.out.println("The workers with " + requestedWorker.hashCode() + " hashCode is alive.");
        //                     try {
        //                         Thread.sleep(1000);
        //                     } catch (InterruptedException e) {
        //                         e.printStackTrace();
        //                     }
        //                 });
        //             } else {
        //                 System.out.println("The worker with the name " +requestedWorker.hashCode()+ " is not alive");

        //                 String workerHashCode = Integer.toString(requestedWorker.hashCode());
        //                 removeWorker(workerHashCode);
                
        //                 nOfWorkers--;
                            
        //                 try (FileWriter writer = new FileWriter(".env")) {
        //                     writer.write("WORKERS=" + nOfWorkers);
        //                     dotenv = Dotenv.configure().directory(".").load();
        //                 } catch (IOException e) {
        //                     System.out.println("An error occurred while updating .env file: " + e.getMessage());
        //                 }
        //                 this.distributingRooms(requestedWorker.getRooms());
        //             }
        //     }
        // }

        // Create and start workers
        for (int i = 0; i < nOfWorkers; i++) {
            String workerName = "Worker" + i;
            String[] command = {"java", "-cp", "your-classpath", "distributed.Worker", workerName};
            
            // Start the worker process
            Process process = Runtime.getRuntime().exec(command);
            
            // Wait for a bit before starting the next worker
            Thread.sleep(1000);
        }

        // Check status of workers
        while (true) {
            for (int j = 0; j < nOfWorkers; j++) {
                String workerName = "Worker" + j;

                // Check if worker process is alive
                Process workerProcess = workerProcesses.get(workerName);

                if (workerProcess.isAlive()) {
                    System.out.println("Worker " + workerName + " is alive.");
                } else {
                    System.out.println("Worker " + workerName + " has died.");
                    ArrayList<Room> missingRooms = workers.get(workerName);
                    removeWorker(workerName);
                
                    nOfWorkers--;
                            
                    try (FileWriter writer = new FileWriter(".env")) {
                        writer.write("WORKERS=" + nOfWorkers);
                        dotenv = Dotenv.configure().directory(".").load();
                    } catch (IOException e) {
                        System.out.println("An error occurred while updating .env file: " + e.getMessage());
                    }
                    this.distributingRooms(missingRooms);

                }
            }
            Thread.sleep(5000); 
        }
    }

    /** 
     * This method creates the workers and distributing all the rooms to them during initialization. 
     * 
     * @throws FileNotFoundException
     * @throws Exception
    **/
    public void createWorkers() throws FileNotFoundException, Exception {
        JSONDirManager manager = new JSONDirManager();

        ArrayList<Hotel> hotels = manager.getHotels();
        ArrayList<Room> rooms = new ArrayList<>();

        for(Hotel hotel: hotels) {
            rooms.addAll(hotel.getRooms());
        }

        this.distributingRooms(rooms);
    }

    /**
     * This method is used, in case a worker "dies", it will distribute its data to the rest of the workers.
     * 
     * @param rooms representing the list of rooms to be allocated.
     **/
    public void distributingRooms(ArrayList<Room> rooms) {
        Mail mail;
        int numberOfActiveWorkers = workers.size();

        for (Room room: rooms) {
            int roomId = room.getIntId();
            int workerIndex = roomId % numberOfActiveWorkers;  // Calculation of the worker's index

            String targetWorkerName = "Worker " + workerIndex; 
            workers.get(targetWorkerName).add(room);

            mail = new Mail("Bookkeeper", targetWorkerName, "room", room);
            System.out.println("Room with hash " + roomId + " distributed to " + targetWorkerName);
        }
    }

    /**
     * This method is used to check if there is a room that has been added. Finds the rooms that have been lost, 
     * stores them in a list and allocate them using the appropriate method.
     * 
     * @throws FileNotFoundException
     * @throws Exception
     **/
    public void checkForMissingRooms() throws FileNotFoundException,Exception {
        ArrayList<Room> existingRooms = this.getAllRooms();
        ArrayList<Room> rooms = new ArrayList<>();

        for (Room room: existingRooms) {
            for (Map.Entry<String, ArrayList<Room>> entry : workers.entrySet()) {
                if (!entry.getValue().contains(room)) {
                    rooms.add(room);
                }
            }
        }

        this.distributingRooms(rooms);
    }

}


