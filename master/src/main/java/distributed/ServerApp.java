package distributed;

import distributed.Server.Server;

/**
 * Server's entry point.
 *
 */
public class ServerApp {
    public static void main( String[] args )
    {
        System.out.println("Hello, I am Server");

        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

    }
}
