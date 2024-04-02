package distributed.Server;

import java.net.Socket;

import distributed.Bookkeeper;

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
