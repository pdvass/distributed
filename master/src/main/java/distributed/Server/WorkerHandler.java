package distributed.Server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import distributed.Bookkeeper;
import distributed.Estate.Hotel;
import distributed.JSONFileSystem.JSONDirManager;
// import distributed.Share.Filter;
import distributed.Share.Tuple;

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
    private Mailbox mailbox = null;
    private JSONDirManager manager = new JSONDirManager();

    public WorkerHandler(Socket s, Response res){
        this.workerSocket = s;
        this.res = res;
        this.bookkeeper.addWorker();
        this.mailbox = new Mailbox();
    }

    public void run(){
        this.populatedMessages();
        this.sendMessagesToWorkers();
    }

    private void populatedMessages(){
        try {
            ArrayList<Hotel> hotels = manager.getHotels();
            for(Hotel hotel : hotels){
                hotel.getRooms().iterator().forEachRemaining(room -> {
                    Tuple msg = new Tuple("room", room);
                    Tuple send = new Tuple("populating", msg);
                    this.res.changeContents(send);
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
        // Filter f = new Filter(new String[]{"stars:3", "dates:[21/04/2024-30/04/2024]"});
        // Tuple msg = new Tuple("client1", f);
        // String transactionMessage = String.format("Transaction opens from %s", HandlerTypes.CLIENT.toString());
        // Tuple transaction = new Tuple("Transaction", transactionMessage);
        // this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.WORKER, "Transaction", transaction);
        // this.mailbox.addMessage(HandlerTypes.CLIENT, HandlerTypes.WORKER, "Filter", msg);
    }

    private void sendMessagesToWorkers(){
        while(true){
            
            ArrayList<Tuple> msgs = this.mailbox.checkMail(HandlerTypes.WORKER);
            // System.out.println("hi");
            if(!msgs.isEmpty()){
                System.out.println("Got a message size->" + msgs.size());
                for(Tuple msg : msgs){
                    this.res.changeContents(msg);
                    try{
                        this.res.sendObject();
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    Tuple response = (Tuple) this.res.readObject();
                    System.out.println("Received object");
                    Tuple toClient = new Tuple(response.getFirst(), response.getSecond());
                    this.mailbox.addMessage(HandlerTypes.WORKER, HandlerTypes.CLIENT, "Message", toClient);
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
