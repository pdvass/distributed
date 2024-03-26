package distributed.Server;

import java.io.*;
import java.net.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clienSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        clienSocket = serverSocket.accept();
        out = new PrintWriter(clienSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
        String greeting = in.readLine();
        while(!greeting.equals("q")){
            System.out.println(greeting);
            greeting = in.readLine();
        }
    }

    public void stop() throws IOException{
        in.close();
        out.close();
        clienSocket.close();
        serverSocket.close();
    }
    
}
