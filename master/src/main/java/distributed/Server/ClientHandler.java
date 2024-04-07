package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import distributed.Bookkeeper;
import distributed.Estate.Hotel;
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
 * @author stellagianno
 */
public class ClientHandler extends Thread {
    private static volatile long totalUsers = 0;
    private long id;
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
        this.id = totalUsers++;
    }

    public void run() {
       
        try {
            String greeting;
            // greeting = this.ois.readUTF();
            greeting = this.res.readMessage();
                

            while(!greeting.equals("q")){

                // NOTE: better if refactored to switch
                if(greeting.equals("filter")) {
                    Filter f = (Filter) this.res.readObject();
                    List<Hotel> filteredHotels =  f.applyFilter();
                    List<String> filteredHotelsStrings = new ArrayList<>();
                    filteredHotels.forEach(hotel -> filteredHotelsStrings.add(hotel.toString()));

                    // this.sendObject(filteredHotelsStrings);
                    this.res.changeContents(filteredHotelsStrings);
                    this.res.sendObject();
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
                    this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.MANAGER, "Message",
                                "Client" + this.id + " says: " + greeting.substring(4));
                }else {
                    // System.out.println(greeting);
                    // this.sendMessage(greeting);
                    this.res.changeContents(greeting);
                    this.res.sendMessage();
                }

                ArrayList<Tuple> mails = mailbox.checkMail(this.type);
                if(!mails.isEmpty()){
                    for(Tuple mail : mails){
                        System.out.println(mail);
                    }
                }
                // greeting = this.ois.readUTF();
                greeting = this.res.readMessage();
            }

            this.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException{
        this.res.close();
        this.clienSocket.close();
        bookkeeper.removeUser();
    }
    
}
