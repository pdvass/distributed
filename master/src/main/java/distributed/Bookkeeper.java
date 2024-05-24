package distributed;

import distributed.Estate.*;
import distributed.JSONFileSystem.JSONDirManager;
import distributed.Server.HandlerTypes;
import distributed.Server.Mailbox;
import distributed.Share.Filter;
import distributed.Share.Mail;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * The Bookkeeper class generally holds the information that exists on workers. Essentially 
 * it should remember the rooms that each worker has, and check if the workers are "alive".
 * 
 * @see Worker
 * @see Hotel
 * 
 * @author pdvass
 * @author panagou
 */

public class Bookkeeper extends Thread {

    private static volatile Map<String, ArrayList<Room>> workers = new HashMap<>();
    private static volatile ArrayList<Hotel> hotels = new ArrayList<>();
    private static volatile ArrayList<String> users = new ArrayList<>();
    private HandlerTypes type = HandlerTypes.BOOKKEEPER;
    private Mailbox mailbox = null;
    private static volatile boolean created = false;

    public Bookkeeper(){
        this.mailbox = new Mailbox();
        if(!created){
            created = true;
            try {
                this.createWorkers();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, ArrayList<Room>> getWorkers(){
        return workers;
    }

    public void addWorker(){
        String workerName = "worker" + workers.size();
        workers.put(workerName, new ArrayList<>());

    }

    public void addUser(){
        String user = "User" + Integer.toString(users.size());
        users.add(user);
    }

    public ArrayList<String> getUsers(){
        return users;
    }

    public void removeUser(){
        users.removeLast();
    }

    public ArrayList<Hotel> getHotels() throws FileNotFoundException,Exception{
        JSONDirManager manager = new JSONDirManager();
        hotels = manager.getHotels();
        return hotels;
    }

    public ArrayList<Room> getRooms(String workerName){
        return workers.get(workerName);
    }

    /**
     * This method returns all the rooms of the hotels that exist in the JSON files.
     * 
     * @return An ArrayList with all the rooms of the JSON files.
     * 
     * @throws FileNotFoundException
     * @throws Exception
     */
    public ArrayList<Room> getAllRooms() throws FileNotFoundException,Exception{
        ArrayList<Hotel> hotels = this.getHotels();
        ArrayList<Room> rooms = new ArrayList<>();

        for (Hotel hotel: hotels) {
            rooms.addAll(hotel.getRooms());
        }
        return rooms;
    }    

    /** 
     * This method is used for initialization. It reads from the env file the number of Workers and 
     * creates the corresponding Map number with the id of each worker as a key. Then distribute the 
     * rooms to existing Workers. 
     * 
     * @throws FileNotFoundException
     * @throws Exception
    **/
    public void createWorkers() throws FileNotFoundException, Exception{
        Dotenv dotenv = Dotenv.load();
        String workersEnv = dotenv.get("WORKERS");

        System.out.println(workersEnv);

        int nOfWorkers = Integer.parseInt(workersEnv);

        for(int i = 0; i < nOfWorkers; i++) {
            this.addWorker();
        }

        JSONDirManager manager = new JSONDirManager();

        ArrayList<Hotel> hotels = manager.getHotels();
        ArrayList<Room> rooms = new ArrayList<>();

        for(Hotel hotel: hotels) {
            rooms.addAll(hotel.getRooms());
        }

        this.distributingRooms(rooms);

        // checkMail is invoked as a Runnable thread so that it
        // can pick up the lock and be scheduled by the Mailbox class
        // to write messages to it and read them from it.
        Runnable task = () -> {this.checkMail(nOfWorkers);};
        Thread t = new Thread(task);
        t.start();
    }


    private void checkMail(int nOfWorkers){
        while (true) {
            ArrayList<Mail> mails = this.mailbox.checkMail(this.type, "bookkeeper");

            if(!mails.isEmpty()){
                for(Mail mail : mails) {

                    if(mail.getSubject().equals("Booked")){
                        String[] info = (String[]) mail.getContents();

                        for(Room room : workers.get(info[0])) {
                            if(Integer.parseInt(info[3]) == room.getIntId()){

                                Date[] dates = new Filter(info).getDateRange();
                                room.book(dates[0], dates[1]);
                                // System.out.println("booked " + mail.getSender());
                                Mail approval = new Mail("bookkeeper", mail.getSender(), "Booked", "Booked successfully", -1);
                                this.mailbox.addMessage(this.type, HandlerTypes.CLIENT, approval);
                            }
                        }

                    } else {

                        for(int i = 0; i < nOfWorkers; i++) {

                            Mail clonedMail = new Mail(mail.getSender(), mail.getRecipient(), mail.getSubject(), mail.getContents(), mail.getTransactionNumber());
                            clonedMail.setRecipient("worker" + i);

                            System.out.println(mail.getSender());
                            System.out.println(clonedMail.getSubject() );
                            this.mailbox.addMessage(this.type, HandlerTypes.WORKER, clonedMail);
                        }

                    }
                }
            }
        }
    }

    /**
     * This method is used for cases where we need to to allocate some rooms to existing workers.
     * 
     * @param rooms representing the list of rooms to be allocated.
     **/
    public void distributingRooms(ArrayList<Room> rooms){
        Mail mail;
    
        int numberOfActiveWorkers = workers.size();

        for (Room room: rooms) {
            int roomId = room.getIntId();
            int workerIndex = Math.abs(roomId % numberOfActiveWorkers);  // Calculation of the worker's index

            String targetWorkerName = "worker" + workerIndex;
            workers.get(targetWorkerName).add(room);

            mail = new Mail("Bookkeeper", targetWorkerName, "room", room, -1);
            this.mailbox.addMessage(this.type, HandlerTypes.WORKER, mail);

            String info = "Room with hash " + roomId + " is to be distributed to " + targetWorkerName;
            JSONDirManager manager = new JSONDirManager();
            manager.logInfo(info);
        }
    }

    /**
     * This method is used to check if there is a room that has been added. Finds the rooms that have been lost, 
     * stores them in a list and allocate them using the appropriate method.
     * 
     * @throws FileNotFoundException
     * @throws Exception
     **/
    public void checkForMissingRooms() throws FileNotFoundException,Exception{
        ArrayList<Room> existingRooms = this.getAllRooms();
        ArrayList<Room> rooms = new ArrayList<>();

        for (Room room: existingRooms) {
            for (Map.Entry<String, ArrayList<Room>> entry : workers.entrySet()) {
                if (!entry.getValue().contains(room)){
                    rooms.add(room);
                }
            }
        }

        this.distributingRooms(rooms);
    }

    /**
     * This method is called when a worker dies and must be allocated the rooms he managed.
     * 
     * @param workerID The id of the worker.
     */
    public void workerDied(String workerID){
        System.out.printf("%s died\n", workerID);

        ArrayList<Room> toRedistribute = new ArrayList<>();
        toRedistribute = workers.get(workerID);
        workers.remove(workerID);

        this.distributingRooms(toRedistribute);
        System.out.println(workers.size());
    }
}




