package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import distributed.Bookkeeper;
import distributed.Share.Mail;

/**
 * Worker Handler is responsible for managing the connection between
 * the server and the worker, by exchanging Requests and Responses. Each 
 * worker has its own WorkerHandler. t communicates with other handlers
 * through Mailbox.
 * 
 * @see distributed.Share.Request
 * @see Mailbox
 * @see Response
 * 
 * @author pdvass
 */
public class WorkerHandler extends Thread {

    private static volatile long totalWorkers = 0;
    private String id;
    @SuppressWarnings("unused")
    private Socket workerSocket = null;
    private Response res = null;
    private Bookkeeper bookkeeper = new Bookkeeper();
    private Mailbox mailbox = null;

    public WorkerHandler(Socket s, Response res) throws UnknownHostException, IOException{
        this.workerSocket = s;
        this.res = res;
        this.bookkeeper.addWorker();
        this.mailbox = new Mailbox();

        if(totalWorkers == Long.MAX_VALUE){
            totalWorkers = 0;
        }
        this.id = "worker" + totalWorkers++;
    }

    public void run(){
        this.sendMessagesToWorkers();
    }

    private void sendMessagesToWorkers(){
        while(true) {
            ArrayList<Mail> msgs = this.mailbox.checkMail(HandlerTypes.WORKER, this.id);

            if(!msgs.isEmpty()){
                
                for(Mail msg : msgs) {
                    this.res.changeContents(msg);
                    try{
                        this.res.sendObject();
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }
}
