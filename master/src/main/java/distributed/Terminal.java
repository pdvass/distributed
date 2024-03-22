package distributed;

import distributed.JSONFileSystem.JSONDirManager;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;;

/**
 * Terminal to parse user input and invoke the methods that are needed.
 * @author pdvass
 */
public class Terminal {
    /**
     * Text printed when the "list" command is given
    */
    private String listText = """
            Available commands:
            - list: Lists Available commands
            - help (h): Prints help text
            - hotels: Lists all hotels with all the rooms they have.
            - add: Adds hotel or room to existing database.
            - remove: Removes hotel or room from existing databse.
            - book: Books a room from a hotel to a given date range.
            """;

    public Terminal(){}

    /**
     * Sets up ant dependencies or setting in the system.
     */
    public void setup(){
        System.out.println("Setting up the system...");
        // Setting up any settings that may occur
        System.out.println("Everything is ready.");
    }

    /**
     * Initiates the terminal to read from Master any commands
     */
    public void init(){
        System.out.println("Welcome to room management system. Type 'list' for available commands.");
        System.out.println("Type 'h' or 'help' for anything else.");
        
        Scanner input = new Scanner(System.in);
        boolean running = true;

        JSONDirManager manager = new JSONDirManager();

        while (running) {
            System.out.print("> ");

            String in = input.nextLine();
            //NOTE: Should put a regex to capture a name with spaces
            String[] commandTokens = in.trim().split(" ");
            switch (commandTokens[0].toLowerCase()) {
                case "quit":
                case "q":
                    System.out.println("Quitting...");
                    running = false;
                    break;
                case "help":
                case "h":
                    this.help(commandTokens);
                    break;
                case "hotels":
                    System.out.println("No hotels yet");
                    break;
                case "list":
                    System.out.println(listText);
                    break;
                case "add":
                    this.add(commandTokens, in, manager);
                    break;
                case "remove":
                    this.remove(commandTokens, in, manager);
                    break;
                case "book":
                    System.out.println("Booked a room");
                    break;
                default:
                    System.out.printf("Command \"%s\" is unknown. Please try again.\n", in);
                    break;
            }
        }

        input.close();
    }

   /**
     * Uses a JSONDirManager to communicate with the file system and invoke
     * the corresponding add function (either for hotel or room)
     * 
     * @param tokens String Array with the input tokenized
     * @param in String of the user's input
     * @param manager JSONDirManager object
     * 
     * @see JSONDirManager
     */
    // NOTE: This - and the other dblike methods - should be moved to another class.
    private void add(String[] tokens, String in, JSONDirManager manager){
        if(tokens.length == 1){
            System.err.println("Not enough arguments");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "hotel":
                try {
                    String[] hotelInfo = this.getCommand("add", "hotel", in);
                    String[] info = hotelInfo[1].trim().split(" ");
                    int availableRooms = Integer.parseInt(info[0].replace('(', ' ').trim());
                    float price = Float.parseFloat(info[1]);
                    String region = tokens[5].replace(')', ' ').trim();
                    if(region.trim().length() == 0){
                        System.err.println("Region is empty.");
                        return;
                    }
                    manager.addHotel(hotelInfo[0], region);
                    System.out.printf("Added hotel %s with %.2f$ per room located at %s. It has %d available rooms.\n", 
                                        hotelInfo[0], price, region, availableRooms);

                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Not enough arguments for hotel. Type \"help add\", to see the syntax.");
                } catch (NumberFormatException e){
                    System.err.println("Either Available Rooms or Price cannot be parsed. Available rooms must be a parseable integer");
                    System.err.println("Price must be a parseable float. Try removing any currency signs.");
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
                break;

            case "room":
                try {
                    String[] hotelInfo = this.getCommand("add", "room", in);
                    String[] dates = hotelInfo[1].split(" ");
                    Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(dates[1]);
                    Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(dates[3]);
                    manager.addRoom(hotelInfo[0], dates[1], dates[3]);
                    System.out.printf("Added to hotel %s date range %s to %s.\n", 
                                hotelInfo[0], startDate.toString(), endDate.toString());
                    
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Not enough arguments for hotel. Type \"help add\", to see the syntax.");
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
                break;
                
            default:        
                System.err.println("Value after add must be \"hotel\" or \"room\".");
                break;
        }
    }

    /**
     * Uses a JSONDirManager to communicate with the file system and invoke
     * the corresponding remove function (either for hotel or room)
     * 
     * @param tokens String Array with the input tokenized
     * @param in String of the user's input
     * @param manager JSONDirManager object
     * 
     * @see JSONDirManager
     */
    private void remove(String[] tokens, String in, JSONDirManager manager){
        if(tokens.length == 2){
            System.err.println("Not enough arguments");
            return;
        }
        String[] hotelInfo = new String[2];
        switch (tokens[1]) {
            case "hotel":
                try{
                    hotelInfo = this.getCommand("remove", "hotel", in);
                    manager.removeHotel(hotelInfo[0]);
                } catch (IndexOutOfBoundsException e){
                    System.err.println("You must give the name of the hotel which you need to remove.");
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
                break;
            case "room":
                try {
                    hotelInfo = this.getCommand("remove", "room", in);
                    int roomId = Integer.parseInt(tokens[2]);
                    manager.removeRoom(hotelInfo[0], roomId);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Not enough arguments. Type \"help remove\", to see th syntax.");
                } catch (NumberFormatException e){
                    System.err.println("RoomID must be a parseable integer");
                } catch (Exception e){
                    System.err.println(e.getMessage());
                }
                break;
            default:
                System.err.println("Value after add must be \"hotel\" or \"room\".");
                break;
        }
    }

    /**
     * Receives a String array with all the tokens read from the command line and
     * prints the appropriate message.
     * @param tokens A String array with the tokens from the command.
     */
    private void help(String[] tokens){
        if(tokens.length == 1){
            System.out.println("You should add the command of which you need help.");
            System.out.println("ex. help book");
            return;
        }
        switch (tokens[1]) {
            case "add":
                System.out.println("\"add\" Adds a hotel or a room to the database. Its syntax is as follows.");
                System.out.println("For hotel: ");
                System.out.println("\t~> add hotel $HOTEL_NAME ($AVAILABLE_ROOMS $PRICE $REGION)");
                System.out.println("For room: ");
                System.out.println("\t~> add room to $HOTEL_NAME from $START_DATE to $END_DATE");
                break;
            
            case "remove":
                System.out.println("\"remove\" Removes a hotel or a room from the database. Its synta is as follows.");
                System.out.println("For hotel: ");
                System.out.println("\t~> remove hotel $HOTEL_NAME");
                System.out.println("For room: ");
                System.out.println("\t~> remove room $ID from $HOTEL_NAME");
                break;
            case "book":
                System.out.println("\"book\" Books a room from a hotel to a given date range. Its syntax is as follows.");
                System.out.println("\t~> book room $ID from $HOTEL_NAME for $NUMBER_OF_DAYS starting $START_DATE");
                System.out.println("Date format should be: dd/mm/yyyy");
                break;
            default:
                System.out.println("We can't help you with that command");
                break;
        }
    }

    /**
     * Isolates the hotel name and remaining info of the command.
     * 
     * @param action The action that will be invoked. Used for taking advantage of its grammar.
     * @param object The object on which the command will be used (hotel or room).
     * @param in User's input
     * @return A String array of size 2 with the hotel name and the remaining info of the command.
     * @throws Exception If the hotel name or info cannot be configured.
     */
    private String[] getCommand(String action, String object, String in) throws Exception{
        // To further understand the regex expressions used for each action and object
        // see analysis on the right side of
        // https://regex101.com/
        String regex = "";
        String[] hotelInfo = new String[2];
        switch (action) {
            case "add":
                switch (object) {
                    case "room":
                        regex = "(?<cmd>add room to)\\s+(?<name>.+)\\s+(?<info>from .+)\\s*";
                        break;
                    case "hotel":
                        regex = "(?<cmd>add hotel)\\s+(?<name>.+)\\s+(?<info>\\(.+\\))\\s*";
                        break;
                    default:
                        break;
                }
                break;
            case "remove":
                switch (object) {
                    case "room":
                        regex = "(?<cmd>remove room .+ from)\\s+(?<name>.+)\\s*(?<info>\\s*)\\s*";
                        break;
                    case "hotel":
                        regex = "(?<cmd>remove hotel)\\s+(?<name>.+)\\s*(?<info>\\s*)\\s*";
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(in);
        if(matcher.find()){
            hotelInfo[0] = matcher.group("name");
            hotelInfo[1] = matcher.group("info");
        } else {
            throw new Exception("Could not configure either hotel name or info.");
        }
        return hotelInfo;
    }
}
