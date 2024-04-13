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

    private Socket reducerConn = null;
    private Request reducerReq = null;

    public Worker() throws UnknownHostException, IOException{
        this.conn = new Socket("localhost", 4555);
        this.req = new Request(this.conn, "worker connection");

        this.reducerConn = new Socket("localhost", 25565);
        this.reducerReq = new Request(this.reducerConn, "worker connection");
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

            this.reducerReq.sendMessage();
            String reducerIsConnected = this.reducerReq.receiveMessage();
            if(!reducerIsConnected.equals("worker connected to reducer")){
                throw new Exception();
            }
            System.out.println("Connected to reducer");

        } catch (Exception e) {
            System.out.println("Could not reach server.");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void init(){
        try{
            Mail incoming = (Mail) this.req.receiveRequestObject();
            // System.out.println("hi");
            // Mail incoming = (Mail) incomingTuple.getSecond();

            while (true) {
                String message = incoming.getSubject();
                System.out.println(incoming.getSender());
    
                if(message.equals("room")){
                    System.out.println("Got a room");
                    Room room = (Room) incoming.getContents();
                    this.rooms.add(room);

                } else if (incoming.getSender().contains("client")) {
                    Object typeOfRequest = incoming.getContents();
                    Filter f = null;
                    try {
                        f = (Filter) typeOfRequest;
                    } catch (Exception e){
                        System.out.println("Error during casting " + e.getMessage());
                    }
                    if(f != null){
                        System.out.println("Applying filters to my room list");
                        List<Room> filteredRoms = f.applyFilter(this.rooms);
                        // Mail response = new Mail(message, filteredRoms);
                        incoming.respond();
                        incoming.setContents(filteredRoms);
                        this.reducerReq.changeContents(incoming);
                        this.reducerReq.sendRequestObject();

                    } else if(typeOfRequest instanceof String && ((String) typeOfRequest).equals("hotels")){
                        System.out.println("Client " + incoming.getSender() + " asked for hotels");
                        incoming.respond();
                        incoming.setContents(this.rooms);
                        this.reducerReq.changeContents(incoming);
                        this.reducerReq.sendRequestObject();
                    }
                } else if (message.equals("manager")){
                    System.out.println("Request received from manager");
                }
    
                incoming = (Mail) this.req.receiveRequestObject();
                // incoming = (Mail) incomingTuple.getSecond();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



