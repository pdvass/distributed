package distributed.Reducer;

import java.io.IOException;
import java.net.Socket;

import distributed.Server.Response;
import distributed.Share.Mail;

/**
 * Client Handler is responsible for managing the connection between
 * the server and the client, by exchanging Requests and Responses. Each 
 * client has its own ReducerHandler.
 * 
 * @see distributed.Share.Request
 * @see Response
 * 
 * 
 * @author stellagianno
 * @author panagou
 */
public class ReducerHandler extends Thread {
    protected static volatile long totalHandlers = 0;
    private Socket workerSocket = null;
    private Response res = null;
    private Merger merger = new Merger();



    public ReducerHandler(Socket socket, Response res){
        this.workerSocket = socket;
        this.res = res;
        if( totalHandlers == Long.MAX_VALUE){
            System.out.println("Max number of handlers reached");
        }
        totalHandlers++;
    }


    public void run(){
        while (true) {
            Mail incoming = (Mail) this.res.readObject();
            merger.receiveMail(incoming);   
        }
    }


    public void close() throws IOException{
        this.res.close();
        this.workerSocket.close();
    }
    
}
