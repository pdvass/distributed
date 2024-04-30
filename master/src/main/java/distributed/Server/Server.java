package distributed.Server;

import java.io.*;
import java.net.*;

/**
 * @author panagou
 * @author stellagianno
 */
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

        while(true) {
            Socket client = serverSocket.accept();
            Response res = new Response(client, null);
            
            String msg = res.readMessage();

            if(msg.equals("user connection")){
                res.changeContents("client connected");
                res.sendMessage();

                ClientHandler responseSocket = new ClientHandler(client, res);
                Thread response = new Thread(responseSocket);
                response.start();

            } else if(msg.equals("worker connection")){
                res.changeContents("worker connected");
                res.sendMessage();

                WorkerHandler responseSocket = new WorkerHandler(client, res);
                Thread response = new Thread(responseSocket);
                response.start();
                
            } else if(msg.equals("Manager connection")){
                res.changeContents("manager connected");
                res.sendMessage();

                ManagerHandler responseSocket = new ManagerHandler(client, res);
                Thread response = new Thread(responseSocket);
                response.start();

            } else if(msg.equals("reducer connection")){
                res.changeContents("reducer connected");
                res.sendMessage();

                ReducerHandler responseSocket = new ReducerHandler(client, res);
                Thread response = new Thread(responseSocket);
                response.start();
            }
        }
    }

    public void close() throws IOException {
        serverSocket.close();
    }
    
}
