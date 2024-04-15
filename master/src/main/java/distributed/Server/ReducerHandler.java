package distributed.Server;

import java.io.IOException;
import java.net.Socket;

// import distributed.Bookkeeper;
import distributed.Share.Mail;

/**
 * ReducerHandler on Server's side is responsible to communicate
 * with the Reducer and forward each result to the Mailbox for the
 * rest of the handlers to see.
 * 
 * @see Mailbox
 * 
 * @see HandlerTypes
 * 
 * @see ClientHandler
 * @see ManagerHandler
 * @see WorkerHandler
 */
public class ReducerHandler extends Thread {
    private Socket reducerSocket = null;
    private Response res = null;
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.REDUCER;

    public ReducerHandler(Socket socket, Response res){
        this.reducerSocket = socket;
        this.res = res;
        this.mailbox = new Mailbox();
    }

    public void run(){
        this.forwardMessage();
    }

    public void forwardMessage(){
        Mail mail = (Mail) this.res.readObject();
        if(mail.getRecipient().equals("manager")){
            this.mailbox.addMessage(this.type, HandlerTypes.MANAGER, mail);
        } else {
            this.mailbox.addMessage(this.type, HandlerTypes.CLIENT, mail);
        }

    }

    public void close(){
        try {
            this.reducerSocket.close();
            this.res.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
