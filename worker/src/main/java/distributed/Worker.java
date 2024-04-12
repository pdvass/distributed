package distributed;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import distributed.Estate.Room;
import distributed.Share.Filter;
import distributed.Share.Request;
import distributed.Share.Mail;

public class Worker extends Thread {
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
            Mail incoming = (Mail) this.req.receiveRequestObject();
            // Mail incoming = (Mail) incomingTuple.getSecond();

            while (true) {
                String message = incoming.getSubject();
                System.out.println(message);
    
                if(message.equals("room")){
                    System.out.println("Got a room");
                    Room room = (Room) incoming.getContents();
                    this.rooms.add(room);
                    Mail dummy = new Mail("", "", "dummy", null);
                    this.req.changeContents(dummy);
                    this.req.sendMessage();
                } else if (incoming.getSender().contains("client")){
                    Object typeOfRequest = incoming.getContents();
                    Filter f = null;
                    try {
                        f = (Filter) typeOfRequest;
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    if(f != null){
                        System.out.println("Applying filters to my room list");
                        List<Room> filteredRoms = f.applyFilter(this.rooms);
                        // Mail response = new Mail(message, filteredRoms);
                        incoming.respond();
                        incoming.setContents(filteredRoms);
                        this.req.changeContents(incoming);
                        this.req.sendRequestObject();

                    } else if(typeOfRequest instanceof String && ((String) typeOfRequest).equals("hotels")){
                        System.out.println("Client " + message + " asked for hotels");
                        for(Room room : this.rooms){
                            this.req.changeContents(room);
                            this.req.sendRequestObject();
                        }
                    }
                } else if (message.equals("manager")){
                    System.out.println("Request received from manager");
                }
    
                incoming = (Mail) this.req.receiveRequestObject();
                // incoming = (Mail) incomingTuple.getSecond();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}



