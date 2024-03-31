package distributed.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
import distributed.Share.Filter;

/**
 * More like ClientHandler. Might rename.
 */
public class Response extends Thread {
    private Socket clienSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public Response(Socket socket){
        this.clienSocket = socket;
    }

    public void run() {
       
        try {
            this.ois = new ObjectInputStream(this.clienSocket.getInputStream());
            this.oos = new ObjectOutputStream(this.clienSocket.getOutputStream());

            String greeting;
            greeting = this.ois.readUTF();
                

            while(!greeting.equals("q")){

                // NOTE: better if refactored to switch
                if(greeting.equals("filter")) {
                    Filter f = (Filter) this.readObject();
                    List<Hotel> filteredHotels =  f.applyFilter();
                    List<String> filteredHotelsStrings = new ArrayList<>();
                    filteredHotels.forEach(hotel -> filteredHotelsStrings.add(hotel.toString()));

                    this.sendObject(filteredHotelsStrings);
                } else if (greeting.equals("hotels")){
                    JSONDirManager manager = new JSONDirManager();
                    List<String> hotels = new ArrayList<>();
                    try {
                        manager.getHotels().stream().forEach(hotel -> hotels.add(hotel.toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        // System.out.println(e.getMessage());
                    } 
                    this.sendObject(hotels);
                } else {
                    // System.out.println(greeting);
                    this.sendMessage(greeting);
                }

                greeting = this.ois.readUTF();
            }

            this.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void sendObject(Object c) throws IOException{
        this.oos.writeObject(c);
        this.oos.flush();
    }

    private Object readObject(){
        Object obj = null;
        try {
            obj =  this.ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private void sendMessage(String msg) throws IOException{
        this.oos.writeUTF(msg);
        this.oos.flush();
    }

    public void close() throws IOException{
        ois.close();
        oos.close();
        clienSocket.close();
    }
}
