package distributed.Server;

import java.io.*;
import java.net.*;

import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
// import distributed.Share.Counter;
import distributed.Share.Filter;

import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private ServerSocket serverSocket = null;
    private Socket clienSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public void run(){
        try {
            this.init(4555);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port) throws IOException{
        this.serverSocket = new ServerSocket(port);
        this.clienSocket = serverSocket.accept();

        this.ois = new ObjectInputStream(this.clienSocket.getInputStream());
        this.oos = new ObjectOutputStream(this.clienSocket.getOutputStream());
        
        // Counter c = new Counter(0);

        // System.out.println("Client connected");
        String greeting = this.ois.readUTF();        

        while(!greeting.equals("q")){

            // NOTE: better if refactored to switch
            if(greeting.equals("filter")) {
                Filter f = (Filter) this.readObject();
                List<Hotel> filteredHotels =  f.applyFilter();
                List<String> filteredHotelsStrings = new ArrayList<>();
                filteredHotels.forEach(hotel -> filteredHotelsStrings.add(hotel.toString()));

                this.sendObject(filteredHotelsStrings);
            } 
            // else if(greeting.equals("GET obj")){
            //     this.sendObject(c);
            //     c = (Counter) this.readObject();
            //     System.out.println(c.getCounter());

            // } 
            else if (greeting.equals("hotels")){
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
        serverSocket.close();
    }
    
}
