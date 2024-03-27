package distributed.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
    private Socket clientSocket = null;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public void startConnection(String ip, int port) throws UnknownHostException, IOException{
        this.clientSocket = new Socket(ip, port);
        this.oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
        this.ois = new ObjectInputStream(this.clientSocket.getInputStream());
    }

    public void sendMessage(String msg) throws IOException{
       this.oos.writeUTF(msg);
       this.oos.flush();
    }

    public String receiveMsg() throws IOException{
        String received = null;
        received = this.ois.readUTF();
        return received;
    }

    public Object receiveObject(){
        Object obj = null;
        try {
            obj = this.ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void sendObject(Object obj){
        try {
            this.oos.writeObject(obj);
            this.oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException{
        this.oos.close();
        this.ois.close();
        clientSocket.close();
    }
}
