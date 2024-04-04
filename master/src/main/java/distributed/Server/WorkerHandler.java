package distributed.Server;

import distributed.Bookkeeper;

import java.util.ArrayList;
import java.io.IOException;
import java.net.Socket;

/**
 * Worker Handler is responsible for managing the connection between
 * the server and the worker, by exchanging Requests and Responses. Each 
 * worker has its own WorkerHandler.
 * 
 * @see distributed.Share.Request
 * @see Response
 * 
 * @author pdvass
 * @author panagou
 */

public class WorkerHandler extends Thread {
    private Socket workerSocket = null;
    private Response res = null;
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.WORKER;
    private Bookkeeper bookkeeper = new Bookkeeper();

    public WorkerHandler(Socket s, Response res) {
        this.workerSocket = s;
        this.res = res;
        this.mailbox = new Mailbox();
    }

    public void run() {

        try {

            String greeting;
            // greeting = this.ois.readUTF();
            greeting = this.res.readMessage();

            while(!greeting.equals("q")) {

                bookkeeper.checkWorkers();

                if(greeting.equals("hotels")) {
                    res.changeContents(this.bookkeeper.getHotels());
                    res.sendObject();
        
                } else if(greeting.equals("filter")) { 

                }

                ArrayList<String> mails = mailbox.checkMail(this.type);
                if(!mails.isEmpty()) {
                    for(String mail : mails) {
                        System.out.println(mail);
                    }
                }

                // greeting = this.ois.readUTF();
                greeting = this.res.readMessage();
            }

            this.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void close() throws IOException {
        this.res.close();
        this.workerSocket.close();
    }

}


