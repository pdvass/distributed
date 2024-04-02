package distributed.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TCPClient is used to communicate with the server and send GET requests for the Client.
 * 
 * @author pdvass
 */
public class TCPClient {
    private Socket clientSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    /**
     * Default constructor
     */
    public TCPClient(){}

    /**
     * Starts the conection with the server given an IP address and the port.
     * 
     * @param ip IP address of the server.
     * @param port The port which the server is listening to.
     * @throws UnknownHostException
     * @throws IOException
     */
    public void startConnection(String ip, int port) throws UnknownHostException, IOException{
        this.clientSocket = new Socket(ip, port);
        //First it sends, then it receives
        this.oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(this.clientSocket.getInputStream());
    }

    /**
     * Send a string message to the server. Mainly used for the type of 
     * request that the client wants to make.
     * 
     * @param msg String message the client wants to send.
     * @throws IOException
     */
    public void sendMessage(String msg) throws IOException{
       this.oos.writeUTF(msg);
       this.oos.flush();
    }

    /**
     * Receive a String response for the server.
     * 
     * @return String representing the server's response.
     * @throws IOException
     */
    public String receiveMsg() throws IOException{
        String received = null;
        received = this.ois.readUTF();
        return received;
    }


    /**
     * Reveive an Object from the server. The client is responsible for
     * understanding what this object is.
     * 
     * @return An Object response from the server.
     */
    public Object receiveObject(){
        Object obj = null;
        try {
            obj = this.ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Send an Object to the server. The server is responsible for
     * understanding what the Object is. The Object must be serializable.
     *  
     * @param obj The Object that needs to be send.
     */
    public void sendObject(Object obj){
        try {
            this.oos.writeObject(obj);
            this.oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the TCPClient.
     * 
     * @throws IOException
     */
    public void stop() throws IOException{
        this.oos.close();
        this.ois.close();
        clientSocket.close();
    }
}
