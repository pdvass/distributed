package distributed.Share;

import java.io.IOException;

import distributed.Client.TCPClient;

public class Request {
    private TCPClient connection = null;
    private Object contents = null;

    public Request(TCPClient conn, Object contents){
        this.connection = conn;
        this.contents = contents;
    }

    public void changeContents(Object newContents){
        this.contents = newContents;
    }

    public void sendMessage() throws IOException{
        this.connection.sendMessage((String) this.contents);
    }

    public String receiveMessage() throws IOException{
        String res = this.connection.receiveMsg();
        return res;
    }

    public void sendRequestObject(){
        this.connection.sendObject(this.contents);
    }

    public Object receiveRequestObject(){
        Object res = this.connection.receiveObject();
        return res;
    }
}
