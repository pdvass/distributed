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
 * client has its own ClientHandler. It communicates with other handlers
 * through Mailbox.
 * 
 * @see distributed.Share.Request
 * @see Mailbox
 * @see Response
 * 
 * @author pdvass
 * @author stellagianno
 */
public class ClientHandler extends Thread {

    private static volatile long totalUsers = 0;

    private static volatile long transactionNumber = 0;
    private String id;
    private Socket clienSocket = null;
    private Response res = null;
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.CLIENT;
    private Bookkeeper bookkeeper = new Bookkeeper();

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
        // Since the class extends Thread, we can create two
        // runnnable lambdas to act as internal threads for 
        // this class.
        Runnable task = () -> {this.send();};
        Runnable task1 = () -> {this.checkMail();};
        Thread t = new Thread(task);
        Thread t1 = new Thread(task1);
        t.start();
        t1.start();

        try{
            t.join();
            t1.join();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void send(){
        try {
            String greeting;
            greeting = this.res.readMessage();

            while(!greeting.equals("q")) {

                if(greeting.equals("filter")){
                    this.incrementTransactionNumber();

                    Filter f = (Filter) this.res.readObject();
                    Mail request = new Mail(this.id, "bookkeeper", "Filter", f, transactionNumber);
                    
                    String contents = String.format("Transaction opens from %s with transaction number %d", this.id, transactionNumber);
                    Mail transaction = new Mail(this.id, "worker", "Transaction", contents, transactionNumber);
                    
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, transaction);  
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, request);

                } else if (greeting.equals("hotels")){
                    this.incrementTransactionNumber();

                    Mail request = new Mail(this.id, "bookkeeper", "Message", "hotels", transactionNumber);
                    String contents = String.format("Transaction opens from %s with transaction number %d", this.id, transactionNumber);
                    Mail transaction = new Mail(this.id, "worker", "Transaction", contents, transactionNumber);
                    
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, transaction);  
                    this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, request);
                    
                } else if(greeting.contains("book")){
                    this.incrementTransactionNumber();

                    String[] info = greeting.split(" ");
                    Mail request = new Mail(this.id, "bookkeeper", "Book", info, transactionNumber);
                    this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.BOOKKEEPER, request);
                    
                } else{
                    this.res.changeContents(greeting);
                    this.res.sendMessage();
                }

                greeting = this.res.readMessage();
            }

            this.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private synchronized void incrementTransactionNumber(){
        transactionNumber = transactionNumber + 1;
    }

    public void checkMail(){
        while (true) {
            ArrayList<Mail> msgs = this.mailbox.checkMail(this.type, this.id);

            if(!msgs.isEmpty()){
                for(Mail msg : msgs) {

                    if(msg.getSubject().equals("Book")){

                        if(msg.getContents() == null){
                            this.res.changeContents("No room available");
                            try {
                                this.res.sendMessage();
                                continue;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        String[] contents = (String[]) msg.getContents();

                        Mail noticeMail = new Mail(this.id, "bookkeeper", "Booked", contents, -1);
                        this.mailbox.addMessage(HandlerTypes.MANAGER, HandlerTypes.BOOKKEEPER, noticeMail);
                    } else if(msg.getSubject().equals("Booked")) {

                        this.res.changeContents("Booked successfully");
                        try {
                            this.res.sendMessage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // We know the server sides leaves the messages as List<Room> 
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
    }

    public void close() throws IOException{
        this.res.close();
        this.clienSocket.close();
        bookkeeper.removeUser();
    }
    
}
