package distributed;

import java.util.ArrayList;

public class Bookkeeper {
    private static volatile ArrayList<String> users = new ArrayList<>();
    private static volatile ArrayList<String> workers = new ArrayList<>();

    public Bookkeeper(){}

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

    public void addWorker(){
        String worker = "Worker" + Integer.toString(workers.size());
        workers.add(worker);
    }

    public ArrayList<String> getWorker(){
        return workers;
    }

    public void removeWorker(){
        workers.removeLast();
    }
}
