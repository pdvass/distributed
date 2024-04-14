package distributed.Server;

import java.io.IOException;
import java.net.Socket;

// import distributed.Bookkeeper;
import distributed.Share.Mail;


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
        this.mailbox.addMessage(this.type, HandlerTypes.CLIENT, mail);

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
