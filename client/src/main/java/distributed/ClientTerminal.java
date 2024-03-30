package distributed;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import distributed.Client.TCPClient;
// import distributed.Share.Counter;
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

            String cmd = this.getCommand(msg);

            switch (cmd) {
                case "filter":
                    this.req.changeContents("filter");
                    this.req.sendMessage();
                    String[] tokens = msg.split(" ");
                    Filter filter = new Filter(tokens);
                    this.req.changeContents(filter);
                    this.req.sendRequestObject();
                    try{
                        @SuppressWarnings("unchecked")
                        List<String> filteredHotels = (List<String>) this.req.receiveRequestObject();
                        filteredHotels.forEach(hotelString -> System.out.println(hotelString));
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "hotels":
                    this.req.changeContents("hotels");
                    this.req.sendMessage();
                    try{
                        @SuppressWarnings("unchecked")
                        List<String> hotels = (List<String>) this.req.receiveRequestObject();
                        hotels.forEach(hotelString -> System.out.println(hotelString));
                    } catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "book":
                    System.out.println("Booking the room for you");
                    break;
                default:
                    this.req.changeContents(msg);
                    this.req.sendMessage();
                    System.out.println(this.req.receiveMessage());
                    break;
            }
            
            // if(msg.equals("GET obj")){
            //     // client.sendMessage(msg);
            //     this.req.changeContents(msg);
            //     this.req.sendMessage();
            //     // Counter c = (Counter) client.receiveObject();
            //     Counter c = (Counter) this.req.receiveRequestObject();
            //     if(c != null){
            //         Counter updatedCounter = new Counter(c.getCounter() + 1);
            //         // client.sendObject(updatedCounter);
            //         this.req.changeContents(updatedCounter);
            //         this.req.sendRequestObject();
            //     } else {
            //         System.out.println("It is null");
            //     }
            // }

            System.out.print("> ");
            msg = scanner.nextLine();
        }

        this.req.changeContents("q");
        this.req.sendMessage();
        client.stop();
        scanner.close();
    }

    public String getCommand(String command){
        String[] tokens = command.split(" ");
        if(tokens.length < 2){
            System.out.println("Not enough arguments");
            return "";
        }

        switch (tokens[0]) {
            case "get":
                if(command.contains("filter")){
                    return "filter";
                }

                if(tokens[1].equals("hotels") && tokens.length == 2){
                    return "hotels";
                }
            case "book":
                return "book";
            default:
                return "";
        }
    }
    
}
