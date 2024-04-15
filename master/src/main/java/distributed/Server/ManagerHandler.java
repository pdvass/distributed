package distributed.Server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import distributed.Bookkeeper;
import distributed.Share.Mail;

/**
 * Manger Handler is responsible for managing the connection between
 * the server and the manager, by exchanging Requests and Responses. The
 * manager connects to the server, through the Terminal.
 * 
 * @see distributed.Terminal
 * @see distributed.Share.Request
 * @see Response
 * 
 * @author pdvass
 */
public class ManagerHandler extends Thread {
    private Socket clienSocket = null;
    private Response res = null;
    private Bookkeeper bookkeeper = new Bookkeeper();
    private Mailbox mailbox = null;
    private HandlerTypes type = HandlerTypes.MANAGER;

    public ManagerHandler(Socket socket, Response res){
        this.clienSocket = socket;
        this.res = res;
        this.mailbox = new Mailbox();
    }

    public void run(){
        while (true) {
            try {
                String request = res.readMessage();
                System.out.println(request);
                switch (request) {
                    case "users":
                        res.changeContents(this.getUsers());
                        res.sendObject();
                        break;
                    case "check":
                        ArrayList<Mail> mails = mailbox.checkMail(this.type, "manager");
                        if(!mails.isEmpty()){
                            for(Mail mail : mails){
                                this.res.changeContents(mail);
                                this.res.sendObject();
                            }
                        } else {
                            Mail empty = new Mail("wokrer", "manager", "Message", "No messsages yet");
                            this.res.changeContents(empty);
                            this.res.sendObject();
                        }
                        Mail finalMsg = new Mail("worker", "manager", "Message", "-1");
                        this.res.changeContents(finalMsg);
                        this.res.sendObject();
                        break;
                    case "show":
                        Object filter = this.res.readObject();
                        Mail managerRequest = new Mail("manager", "bookkeeper", "Filter", filter);
                        this.mailbox.addMessage(this.type, HandlerTypes.BOOKKEEPER, managerRequest);
                        // System.out.println("Left the message ");
                        ArrayList<Mail> bookings = new ArrayList<Mail>();
                        while(bookings.isEmpty()){
                            bookings = this.mailbox.checkMail(this.type, "manager");
                        }
                        // System.out.println("Got the email");
                        @SuppressWarnings("unchecked") 
                        TreeMap<String, Long> ans = (TreeMap<String, Long>) bookings.get(0).getContents();
                        System.out.println(ans.size());
                        ans.forEach((key, value) -> {System.out.println(key + " hi " + value);});
                        this.res.changeContents(ans);
                        this.res.sendObject();
                        break;
                    default:
                        res.changeContents(-1);
                        res.sendObject();
                        break;
                }

            } catch (EOFException e) {
                // Do nothing, as this occurs if the manager decides to log out.

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public int getUsers(){
        return this.bookkeeper.getUsers().size();
    }

    public void close() throws IOException{
        this.res.close();
        this.clienSocket.close();
    }
}
