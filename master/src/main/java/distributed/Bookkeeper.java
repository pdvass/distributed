package distributed;

import distributed.Estate.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.cdimascio.dotenv.Dotenv;

//java -jar .\target\master-1.0-SNAPSHOT-jar-with-dependencies.jar debug

/**
 * The Bookkeeper class generally holds the information that exists on workers. Essentially 
 * it should remember the rooms that each worker has, and check if the workers are "alive".
 * 
 * @author panagou
 * @see Worker
 */

public class Bookkeeper {

    private Map<String, Worker> workers;

    public Bookkeeper() {
        this.workers = new HashMap<String, Worker>();
    }

    public Map<String, Worker> getWorkers() {
        return this.workers;
    }

    public Worker getWorker(String workerName) {
        return workers.get(workerName);
    }

    public void addWorker(String workerName, Worker worker) {
        (this.workers).put(workerName, worker);
    }

    public void addRoom(String workerName, int roomId, Room room) {
        (workers.get(workerName)).addRoom(roomId, room);
    }

    public void removeRoom(String workerName, int roomId) {
        (workers.get(workerName)).removeRoom(roomId);
    }

    /** 
     * This method reads the number of workers from an env file and creates the workers objects. 
     * It also creates a thread for each worker, which checks if the workers are "alive". 
    **/
    public void createWorkers() {

        Dotenv dotenv = Dotenv.load();
        String workersEnv = dotenv.get("WORKERS");

        System.out.println(workersEnv);

        int nOfWorkers = Integer.parseInt(workersEnv);

        for (int i=1; i<=nOfWorkers; i++) {

            Worker worker = new Worker("Worker " + i);
            workers.put(worker.getName(), worker);

        }

        ExecutorService executor = Executors.newFixedThreadPool(nOfWorkers); // One Thread per Worker

        // Thread for checking if workers are alive
        while (true) {
            for (int j=1; j<=nOfWorkers; j++) {
                Worker requestedWorker = workers.get("worker"+j);
    
                    if (requestedWorker.isAlive()) {
                        executor.submit(() -> {
                            System.out.println(requestedWorker.getName() + " is alive.");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        System.out.println("The worker with the name " +requestedWorker.getName()+ " is not alive");
                        this.workers.remove(requestedWorker.getName());
                
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
            worker.sendData("Data to Worker" + worker.getName());
        });

        
    }

    /**
     * This method is used, in case a worker "dies", it will distribute its data to the rest of the workers.
     * @param worker representing the Worker object which has been lost.
     **/
    public void distributingRooms(Worker worker) {
        Map<Integer, Room> rooms = worker.returnRooms();
        int numberOfActiveWorkers = workers.size();

        for (Room room: rooms.values()) {
            int roomId = room.getIntId();
            int workerIndex = roomId % numberOfActiveWorkers;  // Calculation of the worker's index

            String targetWorkerName = "Worker " + workerIndex; 

            addRoom(targetWorkerName, roomId, room);
            System.out.println("Room with hash " + roomId + " distributed to " + targetWorkerName);
        }
    }

}