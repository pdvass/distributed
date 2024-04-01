package distributed.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * More like ClientHandler. Might rename.
 */
public class Response extends Thread {
    private Socket connection = null;
    private Object contents;
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;

    public Response(Socket conn, Object contents) throws IOException{
        this.connection = conn;
        this.contents = contents;
        this.ois = new ObjectInputStream(this.connection.getInputStream());
        this.oos = new ObjectOutputStream(this.connection.getOutputStream());
    }

    protected void changeContents(Object newContents){
        this.contents = newContents;
    }
    
    protected void sendObject() throws IOException{
        this.oos.writeObject(this.contents);
        this.oos.flush();
    }

    protected Object readObject(){
        Object obj = null;
        try {
            obj =  this.ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    protected String readMessage() throws IOException {
        String response = this.ois.readUTF();
        return response;
    }

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
