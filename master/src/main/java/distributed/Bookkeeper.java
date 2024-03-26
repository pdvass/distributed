package distributed;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import distributed.Estate.*;
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

    public Worker getWorker(String workerName) {
        return workers.get(workerName);
    }

    public void addRoom(String workerName, int roomId, Room room) {
        (workers.get(workerName)).addRoom(roomId, room);
    }

    public void removeRoom(String workerName, int roomId) {
        (workers.get(workerName)).removeRoom(roomId);
    }

    // This method reads the number of workers from an env file and creates the workers objects. 
    // It also creates a thread for each worker, which checks if the workers are "alive". 
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
    
                executor.submit(() -> {
                    if (requestedWorker.isAlive()) {
    
                        System.out.println(requestedWorker.getName() + " is alive.");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("The worker with the name " +requestedWorker.getName()+ " is not alive");
                        this.workers.remove(requestedWorker.getName());
                        distributingRooms(requestedWorker);
                    }
                });
            }
        }

    }

    public void sendData(Worker worker) {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.submit(() -> {
            worker.sendData("Data to Worker" + worker.getName());
        });

        
    }

    // This method is used, in case a worker "dies", it will distribute its data to the rest of the workers.
    public void distributingRooms(Worker worker) {
        Map<Integer, Room> rooms = worker.returnRooms();

        for (Room room: rooms.values()) {
        
        }
    }

}