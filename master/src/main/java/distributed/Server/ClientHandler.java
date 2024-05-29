package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

                    String msg = (String) this.res.readObject();
                    String[] tokens = msg.split(" ");
                    Filter filter = new Filter(tokens);

                    System.out.println(filter.getDateRangeString());
                    Mail request = new Mail(this.id, "bookkeeper", "Filter", filter, transactionNumber);
                    
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
                    } else if(msg.getSubject().equals("Review")) {

                        String content = (String) msg.getContents();
                        // hotel:Hilton stars:4
                        Mail noticeMail = new Mail(this.id, "manager", "Review", content, -1);
                        this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.MANAGER, noticeMail);
                    } else {
                        // We know the server sides leaves the messages as List<Room> 
                        @SuppressWarnings("unchecked")
                        List<Room> rooms = (List<Room>) msg.getContents();

                        HashMap<String, ArrayList<String>> response = new HashMap<>();
                        HashMap<String, String> hotels = new HashMap<>();

                        for (Room room : rooms) {
                            String hotelName = room.getName().replaceFirst("Room\\d", "");
                            hotelName = String.join(" ", hotelName.split("(?=\\p{Lu})"));

                            if (response.containsKey(hotelName)) {
                                response.get(hotelName).add(room.toString());
                            } else {
                                ArrayList<String> roomsList = new ArrayList<>();
                                roomsList.add(room.toString());
                                response.put(hotelName, roomsList);
                            }

                            if (!hotels.containsKey(hotelName)) {
                                hotels.put(hotelName, room.getHotelInfo());
                            }
                        }
                        
                        try {
                            this.res.changeContents(response);
                            this.res.sendObject();

                            this.res.changeContents(hotels);
                            this.res.sendObject();
                        } catch (IOException e) {
                            System.out.println("Could not send results to client" + this.id);
                        }

                        String[] filePaths = {
                            "C:\\Users\\User\\Documents\\AUEB\\diethnes.png",
                            "C:\\Users\\User\\Documents\\AUEB\\fourSeasons.png",
                            "C:\\Users\\User\\Documents\\AUEB\\hilton.png",
                            "C:\\Users\\User\\Documents\\AUEB\\hotelCalifornia.png",
                            "C:\\Users\\User\\Documents\\AUEB\\pergamos.png"
                        };

                        List<byte[]> bytesOfImages = new ArrayList<>();
                        List<Integer> bytesLength = new ArrayList<>();
                        Path path = null;
                        byte[] bytes = null;

                        for (String filePath : filePaths) {
                            try {
                                path = Paths.get(filePath);
                                bytes = Files.readAllBytes(path);
                                bytesOfImages.add(bytes);
                                bytesLength.add(bytes.length);
                            } catch (Exception e) {
                                System.out.println("Error editing image: " + filePath);
                                e.printStackTrace();
                            }
                        }

                        try {
                            this.res.changeContents(bytesOfImages);
                            this.res.sendObject();

                            this.res.changeContents(bytesLength);
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
