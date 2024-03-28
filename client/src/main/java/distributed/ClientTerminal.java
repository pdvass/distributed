package distributed;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import distributed.Client.TCPClient;
import distributed.Share.Counter;

public class ClientTerminal {

    public ClientTerminal(){}

    public void run() throws UnknownHostException, IOException{
        TCPClient client = new TCPClient();
        client.startConnection("localhost", 4555);

        System.out.print("> ");
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

            System.out.print("> ");
            msg = scanner.nextLine();
        }
        client.sendMessage("q");
        client.stop();
        scanner.close();
    }
    
}
