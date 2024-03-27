package distributed;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import distributed.Server.Counter;
import distributed.Server.TCPClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {
        TCPClient client = new TCPClient();
        client.startConnection("localhost", 4555);

        Scanner scanner = new Scanner(System.in);
        String msg = scanner.nextLine();


        while (!msg.equals("q")) {

            if(msg.equals("GET obj")){
                client.sendMessage(msg);
                Counter c = (Counter) client.receiveObject();
                if(c != null){
                    Counter updatedCounter = new Counter(c.getCounter() + 1);
                    client.sendObject(updatedCounter);
                } else {
                    System.out.println("It is null");
                }
            } else {
                client.sendMessage(msg);
                System.out.println(client.receiveMsg());
            }
            msg = scanner.nextLine();
        }
        client.sendMessage("q");
        client.stop();
        scanner.close();
    }
}
