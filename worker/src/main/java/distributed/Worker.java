package distributed;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import distributed.Estate.Room;
import distributed.Share.Filter;
import distributed.Share.Request;
import distributed.Share.Tuple;

public class Worker extends Thread{
    private ArrayList<Room> rooms = null;
    private Socket conn = null;
    private Request req = null;

    public Worker() throws UnknownHostException, IOException{
        this.conn = new Socket("localhost", 4555);
        this.req = new Request(this.conn, "worker connection");
        this.rooms = new ArrayList<>();
    }

    public void run(){
        this.connect();
        this.init();
    }

    public void connect(){
        try {
            this.req.sendMessage();
            System.out.println("Sent!");
            String isConnected = this.req.receiveMessage();
            if(!isConnected.equals("worker connected")){
                throw new Exception();
            }
            System.out.println("Connected to server");
        } catch (Exception e) {
            System.out.println("Could not reach server.");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void init(){
        try{
            Tuple incomingTuple = (Tuple) this.req.receiveRequestObject();
            Tuple incoming = (Tuple) incomingTuple.getSecond();

            while (true) {
                String message = incoming.getFirst();
    
                if(message.equals("room")){
                    Room room = (Room) incoming.getSecond();
                    this.rooms.add(room);
                } else if (message.contains("client")){
                    Object typeOfRequest = incoming.getSecond();
                    Filter f = null;
                    try {
                        f = (Filter) typeOfRequest;
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    if(f != null){
                        List<Room> filteredRoms = f.applyFilter(this.rooms);
                        Tuple response = new Tuple(message, filteredRoms);
                        this.req.changeContents(response);
                        this.req.sendRequestObject();

                    } else if(typeOfRequest instanceof String && ((String) typeOfRequest).equals("hotels")){
                        System.out.println("Client " + message + " asked for hotels");
                    }
                } else if (message.equals("manager")){
                    System.out.println("Request received from manager");
                }
    
                incomingTuple = (Tuple) this.req.receiveRequestObject();
                incoming = (Tuple) incomingTuple.getSecond();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
