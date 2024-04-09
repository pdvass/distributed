package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import distributed.Bookkeeper;
// import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
import distributed.Share.Filter;
import distributed.Share.Tuple;

/**
 * Client Handler is responsible for managing the connection between
 * the server and the client, by exchanging Requests and Responses. Each 
 * client has its own ClientHandler.
 * 
 * @see distributed.Share.Request
 * @see Response
 * 
 * @author pdvass
 */
public class ClientHandler extends Thread {
    private static volatile long totalUsers = 0;
    private long id;
    private Socket clienSocket = null;
    private Response res = null;
    private Bookkeeper bookkeeper = new Bookkeeper();
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.CLIENT;
    private Object lock;

    public ClientHandler(Socket socket, Response res){
        this.clienSocket = socket;
        this.res = res;
        this.bookkeeper.addUser();
        this.mailbox = new Mailbox();
        this.lock = this.mailbox.getLock();
        if(totalUsers == Long.MAX_VALUE){
            totalUsers = 0;
        }
        this.id = totalUsers++;
    }

    public void run(){
        Runnable task = () -> {this.send();};
        Runnable task1 = () -> {this.checkMail();};
        Thread t = new Thread(task);
        Thread t1 = new Thread(task1);
        t.start();
        t1.start();

        try{
            t.join();
            t1.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send() {
        try {
            String greeting;
            // greeting = this.ois.readUTF();
            greeting = this.res.readMessage();
                

            while(!greeting.equals("q")){

                // NOTE: better if refactored to switch
                if(greeting.equals("filter")) {

                    Filter f = (Filter) this.res.readObject();
                    System.out.println("Got a filter");

                    Tuple request = new Tuple("client" + this.id, f);
                    // List<Hotel> filteredHotels =  f.applyFilter();
                    // List<String> filteredHotelsStrings = new ArrayList<>();
                    // filteredHotels.forEach(hotel -> filteredHotelsStrings.add(hotel.toString()));
                    String contents = String.format("Transaction opens from client%d", this.id);
                    synchronized (this.lock){
                        try{
                            this.lock.wait();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        this.mailbox.addMessage(this.type, HandlerTypes.WORKER, "Transaction", contents);
    
                        try{
                            this.lock.wait();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        this.mailbox.addMessage(this.type, HandlerTypes.WORKER, "Filter", request);
                    }
                    // this.sendObject(filteredHotelsStrings);
                    // this.res.changeContents(filteredHotelsStrings);
                    // this.res.sendObject();
                } else if (greeting.equals("hotels")){
                    JSONDirManager manager = new JSONDirManager();
                    List<String> hotels = new ArrayList<>();
                    try {
                        manager.getHotels().stream().forEach(hotel -> hotels.add(hotel.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        // System.out.println(e.getMessage());
                    } 
                    // this.sendObject(hotels);
                    this.res.changeContents(hotels);
                    this.res.sendObject();
                } else if(greeting.contains("say")) {
                    synchronized (this.lock){
                        try{
                            this.lock.wait();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.MANAGER, "Message",
                                    "Client" + this.id + " says: " + greeting.substring(4));
                    }
                } else {
                    // System.out.println(greeting);
                    // this.sendMessage(greeting);
                    this.res.changeContents(greeting);
                    this.res.sendMessage();
                }

                // greeting = this.ois.readUTF();
                greeting = this.res.readMessage();
            }

            this.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkMail(){
        while (true) {
            synchronized (this.lock){
                try{
                    this.lock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                ArrayList<Tuple> msgs = this.mailbox.checkMail(this.type);
                if(!msgs.isEmpty()){
                    System.out.println("Got a message for client size->" + msgs.size());
                    for(Tuple msg : msgs){
                        System.out.println(msg.getFirst());
                    }
                }
            }
        }
    }

    public void close() throws IOException{
        this.res.close();
        this.clienSocket.close();
        bookkeeper.removeUser();
    }
    
}
