package distributed;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

import distributed.Client.TCPClient;
import distributed.Share.Filter;
import distributed.Share.Request;

/**
 * ClientTerminal is used for the Client to use from the cmd to send requests to the server.
 * 
 * @see Request
 * @author pdvass
 */
public class ClientTerminal {

    private Request req = null;
    private final String commands = """
            Available commands:
              - get hotels: Gets all hotels available.
              - get hotels filter $FILTER: get all the hotels available, that comply with the filter.
                Filters can check the following things:
                  * stars: The lower bound of stars, that the hotel must have. Must be float number.
                  * region: The region that the hotel must be located.
                  * dates: The dates the rooms must be available (Formatted as [StartDate-EndDate]).
                  * nOfPersons: The number of persons that the room must have. Must be an integer number.
              - book: Book specific room.
            """;

    public ClientTerminal(){}

    public void run() throws UnknownHostException, IOException{
        TCPClient client = new TCPClient();
        client.startConnection("localhost", 4555);

        this.req = new Request(client, "");

        System.out.println("Welcome to out Booking agency. Type list for available commands.");
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
                        // We already know from server side, that we need to cast to List<String>
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
                        // We already know from server side, that we need to cast to List<String>
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
                case "h":
                    System.out.println("Helped you");
                    break;
                case "commands":
                    System.out.println(this.commands);
                default:
                    this.req.changeContents(msg);
                    this.req.sendMessage();
                    System.out.println(this.req.receiveMessage());
                    break;
            }

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
        
        switch (tokens[0]) {
            case "get":

                if(tokens.length < 2){
                    System.out.println("Not enough arguments");
                    return "";
                }

                if(command.contains("filter")){
                    return "filter";
                }

                if(tokens[1].equals("hotels") && tokens.length == 2){
                    return "hotels";
                }
            case "book":
                return "book";
            case "commands":
                return "commands";
            default:
                return "";
        }
    }
}
