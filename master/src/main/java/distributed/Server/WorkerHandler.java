package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import distributed.Bookkeeper;
import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
// import distributed.Share.Filter;
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
    private Socket workerSocket = null;
    private Response res = null;
    private Bookkeeper bookkeeper = new Bookkeeper();
    private Mailbox mailbox = null;
    private JSONDirManager manager = new JSONDirManager();

    public WorkerHandler(Socket s, Response res){
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
        this.populatedMessages();
        this.sendMessagesToWorkers();
    }

    ////////////////////////////////
    // Should remove this after Bookkeeper's completion.
    private void populatedMessages(){
        try {
            ArrayList<Hotel> hotels = manager.getHotels();
            for(Hotel hotel : hotels){
                hotel.getRooms().iterator().forEachRemaining(room -> {
                    Mail msg = new Mail("manager", this.id, "room", room);
                    // Mail send = new Mail("manager", this.id, "populating", msg);
                    this.res.changeContents(msg);
                    try {
                        this.res.sendObject();
                    } catch (IOException e) {
                        e.printStackTrace();
                    };
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ////////////////////////////////

    /**
     * Check if there are mails for the workers in the mailbox and
     * if there are, forward them to the assigned wokrer.
     * 
     */
    private void sendMessagesToWorkers(){
        while(true){
            ArrayList<Mail> msgs = this.mailbox.checkMail(HandlerTypes.WORKER, this.id);
            // System.out.println("hi");
            if(!msgs.isEmpty()){
                // System.out.println("Got a message size->" + msgs.size());
                for(Mail msg : msgs){
                    this.res.changeContents(msg);
                    try{
                        this.res.sendObject();
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    Mail response = (Mail) this.res.readObject();
                    this.mailbox.addMessage(HandlerTypes.WORKER, HandlerTypes.CLIENT, response);
                }
            }
            
        }
    }

    public void getWorkers(){
        this.bookkeeper.getWorker().size();
    }

    public void close() throws IOException{
        this.res.close();
        this.workerSocket.close();
    }
}
