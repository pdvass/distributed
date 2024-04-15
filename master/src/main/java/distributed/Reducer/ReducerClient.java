package distributed.Reducer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * ReducerClient is used to communicate with the manager and server and receives objects from the Workers.
 * 
 * @author stellagianno
 * @author panagou
 */
public class ReducerClient {
    private Socket clientSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    /**
     * Default constructor
     */
    public ReducerClient(){}

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
     * Send a string message to the server. 
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
     * Stop the ReducerClient.
     * 
     * @throws IOException
     */
    public void stop() throws IOException{
        this.oos.close();
        this.ois.close();
        clientSocket.close();
    }


}

