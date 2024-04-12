package distributed.Reducer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import distributed.Response;

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

        // The reducer accepts results from worker i of the n workers
        // where i= the id of the worker.
        while(true){

            Socket workerSocket = reducerSocket.accept();
            ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream());
            Response res = new Response(workerSocket, null);

            // Send message of connection.
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
        serverSocket.close();
    }
}
