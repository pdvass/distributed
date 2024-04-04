package distributed.Server;

import java.net.Socket;

import distributed.Bookkeeper;

/**
 * Worker Handler is responsible for managing the connection between
 * the server and the worker, by exchanging Requests and Responses. Each 
 * worker has its own WorkerHandler.
 * 
 * @see distributed.Share.Request
 * @see Response
 * 
 * @author pdvass
 */
public class WorkerHandler extends Thread {
    private Socket workerSocket = null;
    private Response res = null;
    private Bookkeeper bookkeeper = new Bookkeeper();

    public WorkerHandler(Socket s, Response res){
        this.workerSocket = s;
        this.res = res;
    }

    public void run(){

    }

    public void getWorkers(){
        
    }
}
