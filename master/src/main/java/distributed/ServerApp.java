package distributed;

import distributed.Server.Server;

public class ServerApp {
    public static void main( String[] args )
    {

        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

        System.out.println("Hello");
    }
}