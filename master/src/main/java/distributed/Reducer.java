package distributed;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import distributed.Response;

public class Reducer extends Thread{
    private ServerSocket reducerSocket = null;

    private final int PORT = 4555;

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
        int n=1;
        ArrayList ResultList = new ArrayList<>();

        //The reducer accepts results from worker n of the 3 workers
        while(!(n==4)){
            n++;
            
            Socket workerSocket = reducerSocket.accept();
            ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream());
            Response res = new Response(workerSocket, null);

            // Send message of connection.
            String msg = res.readMessage();
            res.changeContents("worker" + n + " connected to reducer");
            res.sendMessage();

            // Read tuple from client
            Tuple tuple = (Tuple) in.readObject();
            System.out.println("Received tuple from client: " + tuple);

            int workerId = tuple.getFirst();
            ArrayList list = tuple.getSecond();

            ResultList.add(list);

        }
         
    }
}
