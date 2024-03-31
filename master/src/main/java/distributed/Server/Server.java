package distributed.Server;

import java.io.*;
import java.net.*;


public class Server extends Thread {
    private ServerSocket serverSocket = null;

    private final int PORT = 4555;

    public void run(){
        try {
            this.init(this.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port) throws IOException{
        this.serverSocket = new ServerSocket(port);
        this.serverSocket.setReuseAddress(true);

        while(true){
            Socket client = serverSocket.accept();
 
            Response responseSocket = new Response(client);
            Thread res = new Thread(responseSocket);
            res.start();
        }
        
    }

    public void close() throws IOException {
        serverSocket.close();
    }
    
}
