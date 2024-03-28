package distributed;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import distributed.Client.TCPClient;
import distributed.Share.Counter;
import distributed.Share.Filter;
import distributed.Share.Request;

public class ClientTerminal {

    private Request req = null;

    public ClientTerminal(){}

    public void run() throws UnknownHostException, IOException{
        TCPClient client = new TCPClient();
        client.startConnection("localhost", 4555);

        this.req = new Request(client, "");

        System.out.print("> ");
        Scanner scanner = new Scanner(System.in);
        String msg = scanner.nextLine();
        
        while (!msg.equals("q")) {
            
            if(msg.equals("filter")){
                this.req.changeContents("filter");
                this.req.sendMessage();
                Filter filter = new Filter("region:Kallithea", "dates:[21/04/2024, 23/04/2024]");
                this.req.changeContents(filter);
                this.req.sendRequestObject();
                
            } else if(msg.equals("GET obj")){
                // client.sendMessage(msg);
                this.req.changeContents(msg);
                this.req.sendMessage();
                // Counter c = (Counter) client.receiveObject();
                Counter c = (Counter) this.req.receiveRequestObject();
                if(c != null){
                    Counter updatedCounter = new Counter(c.getCounter() + 1);
                    // client.sendObject(updatedCounter);
                    this.req.changeContents(updatedCounter);
                    this.req.sendRequestObject();
                } else {
                    System.out.println("It is null");
                }
            } else {
                // client.sendMessage(msg);
                this.req.changeContents(msg);
                this.req.sendMessage();
                // System.out.println(client.receiveMsg());
                System.out.println(this.req.receiveMessage());
            }

            System.out.print("> ");
            msg = scanner.nextLine();
        }

        this.req.changeContents("q");
        this.req.sendMessage();
        client.stop();
        scanner.close();
    }
    
}
