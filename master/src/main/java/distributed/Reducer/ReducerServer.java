package distributed.Reducer;

import distributed.Server.Response;

import java.io.*;
import java.net.*;

/**
 * @author stellagianno
 * @author panagou
 */
public class ReducerServer extends Thread{

    private ServerSocket reducerSocket = null;
    private final int PORT = 25565;

    public void run(){
        try {
            this.init(this.PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(int port) throws IOException{

        this.reducerSocket = new ServerSocket(port);
        this.reducerSocket.setReuseAddress(true);

        while(true) {

            Socket workerSocket = reducerSocket.accept();
            Response res = new Response(workerSocket, null);
            String msg = res.readMessage();

            if(msg.equals("worker connection")){
                res.changeContents("worker connected to reducer"); 
                res.sendMessage();

                ReducerHandler responseSocket = new ReducerHandler(workerSocket, res);
                Thread response = new Thread(responseSocket);
                response.start();
            }
        }
         
    }

    public void close() throws IOException {
        reducerSocket.close();
    }
}
