package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
import distributed.Share.Filter;

public class ClientHandler extends Thread {
    private Socket clienSocket = null;
    private Response res = null;

    public ClientHandler(Socket socket){
        this.clienSocket = socket;
    }

    public void run() {
       
        try {
            this.res = new Response(this.clienSocket, null);

            // Figure out type of connections made.
            if(this.res.readMessage().equals("user connection")){
                this.res.changeContents("client connected");
                this.res.sendMessage();
            }

            if(this.res.readMessage().equals("worker connection")){
                this.res.changeContents("worker connected");
                this.res.sendMessage();
            }

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

    public void close() throws IOException{
        this.res.close();
        this.clienSocket.close();
    }
    
}
