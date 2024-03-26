package distributed;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import distributed.ClientServer.TCPClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {
        System.out.println( "Hello World!" );
        TCPClient client = new TCPClient();
        client.startConnection("localhost", 6565);
        Scanner scanner = new Scanner(System.in);
        String msg = scanner.nextLine();
        while (!msg.equals("q")) {
            client.sendMessage(msg);
            // scanner.next();
            msg = scanner.nextLine();
        }
        client.sendMessage("q");
        client.stop();
        scanner.close();
    }
}
