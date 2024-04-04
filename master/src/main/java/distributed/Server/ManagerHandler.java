package distributed.Server;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import distributed.Bookkeeper;

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
                switch (request) {
                    case "users":
                        res.changeContents(this.getUsers());
                        res.sendObject();
                        break;
                    case "check":
                        ArrayList<String> mails = mailbox.checkMail(this.type);
                        if(!mails.isEmpty()){
                            for(String mail : mails){
                                this.res.changeContents(mail);
                                this.res.sendMessage();
                            }
                        } else {
                            this.res.changeContents("No messsages yet");
                            this.res.sendMessage();
                        }
                        this.res.changeContents("-1");
                        this.res.sendMessage();
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
