package distributed.Server;

import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket = null;
    private Socket clienSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public void start(int port) throws IOException{
        this.serverSocket = new ServerSocket(port);
        this.clienSocket = serverSocket.accept();

        this.ois = new ObjectInputStream(this.clienSocket.getInputStream());
        this.oos = new ObjectOutputStream(this.clienSocket.getOutputStream());
        
        Counter c = new Counter(0);

        String greeting = this.ois.readUTF();
        System.out.println(greeting);
        

        while(!greeting.equals("q")){
            
            if(greeting.equals("GET obj")){
                this.sendObject(c);
                c = (Counter) this.readObject();
                System.out.println(c.getCounter());
            } else {
                System.out.println(greeting);
                this.sendMessage(greeting);
            }
            greeting = this.ois.readUTF();
        }
    }

    private void sendObject(Counter c) throws IOException{
        System.out.println(c.getCounter());
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

    public void stop() throws IOException{
        ois.close();
        oos.close();
        clienSocket.close();
        serverSocket.close();
    }
    
}
