package distributed.Share;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
   private Socket connection = null;
    private Object contents;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    /**
     * Basic constructor that takes the TCPClient Connection and the initial Contents
     * that should be sent.
     * 
     * @param conn TCPClient connection, connected with the server.
     * @param contents Contents of the message abstracted to an Object.
     * @throws IOException 
     * 
     * @apiNote Connection must be enstablished before initializing the Request Constructor.
     */
    public Request(Socket conn, Object contents) throws IOException{
        this.connection = conn;
        this.contents = contents;
        //First it sends, then it receives
        this.oos = new ObjectOutputStream(this.connection.getOutputStream());
        this.ois = new ObjectInputStream(this.connection.getInputStream());
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
     */
    public void sendMessage() throws IOException{
        this.oos.writeUTF((String) this.contents);
        this.oos.flush();
    }

    /**
     * Receives a string message through the enstablished connection.
     *
     * @throws IOException
     * 
     */
    public String receiveMessage() throws IOException{
        String res = this.ois.readUTF();
        return res;
    }

    /**
     * Sends an Object through the enstablished connection.
     * @throws IOException 
     * 
     */
    public void sendRequestObject() throws IOException{
        this.oos.writeObject(this.contents);
        this.oos.flush();
    }

     /**
     * Receives a Object through the enstablished connection.
     * @throws IOException 
     * @throws ClassNotFoundException 
     *
     */
    public Object receiveRequestObject() throws ClassNotFoundException, IOException{
        Object res = this.ois.readObject();
        return res;
    }
}
