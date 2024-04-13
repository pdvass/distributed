package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import distributed.Bookkeeper;
import distributed.Estate.Room;
import distributed.Share.Filter;
import distributed.Share.Mail;

/**
 * Client Handler is responsible for managing the connection between
 * the server and the client, by exchanging Requests and Responses. Each 
 * client has its own ClientHandler.
 * 
 * @see distributed.Share.Request
 * @see Response
 * 
 * @author pdvass
 * @author stellagianno
 */
public class ClientHandler extends Thread {
    private static volatile long totalUsers = 0;
    private String id;
    private Socket clienSocket = null;
    private Response res = null;
    private Bookkeeper bookkeeper = new Bookkeeper();
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.CLIENT;

    public ClientHandler(Socket socket, Response res){
        this.clienSocket = socket;
        this.res = res;
        this.bookkeeper.addUser();
        this.mailbox = new Mailbox();
        if(totalUsers == Long.MAX_VALUE){
            totalUsers = 0;
        }
        this.id = "client" + totalUsers++;
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
                    // System.out.println("Got a filter");
                    
                    Mail request = new Mail(this.id, "bookkeeper", "Filter", f);
                    
                    String contents = String.format("Transaction opens from %s", this.id);
                    Mail transaction = new Mail(this.id, "worker", "Transaction", contents);
                    
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, transaction);  
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, request);

                } else if (greeting.equals("hotels")){
                    Mail request = new Mail(this.id, "bookkeeper", "Message", "hotels");
                    String contents = String.format("Transaction opens from %s", this.id);
                    Mail transaction = new Mail(this.id, "worker", "Transaction", contents);
                    
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, transaction);  
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, request);
                    
                } else if(greeting.contains("say")) {
                    String said = this.id + " says: " + greeting.substring(4);
                    Mail request = new Mail(this.id, "manger", "Message", said);
                    this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.MANAGER, request);
                    
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
            ArrayList<Mail> msgs = this.mailbox.checkMail(this.type, this.id);
            if(!msgs.isEmpty()){
                // System.out.println("Got a message for client size->" + msgs.size());
                for(Mail msg : msgs){
                    @SuppressWarnings("unchecked")
                    List<Room> response = (List<Room>) msg.getContents();
                    ArrayList<String> toClient = new ArrayList<String>();
                    response.iterator().forEachRemaining(room -> toClient.add(room.toString()));
                    this.res.changeContents(toClient);
                    try {
                        this.res.sendObject();
                    } catch (IOException e) {
                        System.out.println("Could not send results to client" + this.id);
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
