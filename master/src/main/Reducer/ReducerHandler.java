package distributed.Reducer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import distributed.Estate.Room;
// import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
import distributed.Server.HandlerTypes;
import distributed.Share.Filter;
import distributed.Share.Mail;

import java.util.HashMap;
import java.util.Map;

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
 */
public class ReducerHandler extends Thread {
    protected static volatile long totalHandlers = 0;
    private String id;
    private Socket workerSocket = null;
    private Response res = null;
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.WORKER;
    Merger merger = new Merger();



    public ReducerHandler(Socket socket, Response res){
        this.workerSocket = socket;
        this.res = res;
        if( totalHandlers == Long.MAX_VALUE){
         totalHandlers = 0;
        }
        this.id = "worker" + totalHandlers++;
    }


    public void run(){
        Mail incoming = (Mail) this.res.receiveRequestObject();
        merger.receiveMail(incoming);
    }


    public void close() throws IOException{
        this.res.close();
        this.workerSocket.close();
    }
    
}
