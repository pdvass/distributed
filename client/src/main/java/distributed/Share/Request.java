package distributed.Share;

import java.io.IOException;
import distributed.Client.TCPClient;

/**
 * Request is used to abstract the connection from the Terminal. Works as
 * a middle man between TCPClient and ClientTerminal.
 * 
 * @see TCPClient
 * @see distributed.ClientTerminal
 * 
 * @author pdvass
 */
public class Request {

    private TCPClient connection = null;
    private Object contents = null;

    /**
     * Basic constructor that takes the TCPClient Connection and the initial Contents
     * that should be sent.
     * 
     * @param conn TCPClient connection, connected with the server.
     * @param contents Contents of the message abstracted to an Object.
     * 
     * @apiNote Connection must be enstablished before initializing the Request Constructor.
     */
    public Request(TCPClient conn, Object contents){
        this.connection = conn;
        this.contents = contents;
    }

    /**
     * Change the contents of the Request to send.
     * 
     * @param newContents Contents of the message abstracted to an Object.
     */
    public void changeContents(Object newContents){
        this.contents = newContents;
    }

    /**
     * Sends a string message through the enstablished connection.
     * Request class is responsible for casting it to String.
     *
     * @throws IOException
     * 
     * @see {@link TCPClient#sendMessage(String)}
     */
    public void sendMessage() throws IOException{
        this.connection.sendMessage((String) this.contents);
    }

    /**
     * Receives a string message through the enstablished connection.
     *
     * @throws IOException
     * 
     * @see {@link TCPClient#receiveMsg()}
     */
    public String receiveMessage() throws IOException{
        String res = this.connection.receiveMsg();
        return res;
    }

    /**
     * Sends an Object through the enstablished connection.
     * 
     * @see {@link TCPClient#sendObject(Object)}
     */
    public void sendRequestObject(){
        this.connection.sendObject(this.contents);
    }

     /**
     * Receives a Object through the enstablished connection.
     *
     * @see {@link TCPClient#receiveObject()}
     */
    public Object receiveRequestObject(){
        Object res = this.connection.receiveObject();
        return res;
    }
}
