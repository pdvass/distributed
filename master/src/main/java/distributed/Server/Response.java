package distributed.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Request is used to abstract the connection from the Terminal. Works as
 * a middle man between ClientHandler and Server.
 * 
 * @see distributed.Server.ClientHandler
 * @see distributed.Server.Server
 * 
 * @author pdvass
 */
public class Response extends Thread {
    private Socket connection = null;
    private Object contents;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    /**
     * Basic constructor that takes the Connection and the initial Contents
     * that should be sent.
     * 
     * @param conn Socket connection, connected with the client.
     * @param contents Contents of the message abstracted to an Object.
     * 
     * @apiNote Connection must be enstablished before initializing the Request Constructor.
     */
    public Response(Socket conn, Object contents) throws IOException{
        this.connection = conn;
        this.contents = contents;
        this.ois = new ObjectInputStream(this.connection.getInputStream());
        this.oos = new ObjectOutputStream(this.connection.getOutputStream());
    }

    /**
     * Change the contents of the Request to send.
     * 
     * @param newContents Contents of the message abstracted to an Object.
     */
    protected void changeContents(Object newContents){
        this.contents = newContents;
    }
    
    /**
     * Sends an Object through the enstablished connection.
     */
    protected void sendObject() throws IOException{
        this.oos.writeObject(this.contents);
        this.oos.flush();
    }

    /**
     * Receives a Object through the enstablished connection.
     */
    protected Object readObject(){
        Object obj = null;
        try {
            obj =  this.ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

     /**
     * Receives a string message through the enstablished connection.
     *
     * @throws IOException
     */
    protected String readMessage() throws IOException {
        String response = this.ois.readUTF();
        return response;
    }

    /**
     * Sends a string message through the enstablished connection.
     * response class is responsible for casting it to String.
     *
     * @throws IOException
     */
    protected void sendMessage() throws IOException {
        this.oos.writeUTF((String) this.contents);
        this.oos.flush();
    }

    protected void close() throws IOException{
        this.ois.close();
        this.oos.close();
        this.connection.close();
    }
}
