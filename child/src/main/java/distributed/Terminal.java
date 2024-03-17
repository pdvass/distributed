package distributed;

import java.util.Scanner;

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

        while (running) {
            System.out.print("> ");

            String in = input.nextLine();
            String[] commandTokens = in.toLowerCase().split(" ");
            switch (commandTokens[0]) {
                case "quit":
                case "q":
                    System.out.println("Quitting...");
                    running = false;
                    break;
                case "help":
                case "h":
                    help(commandTokens);
                    break;
                case "hotels":
                    System.out.println("No hotels yet");
                    break;
                case "list":
                    System.out.println(listText);
                    break;
                case "add":
                    System.out.println("Added a hotel");
                    break;
                case "remove":
                    System.out.println("Remmoved a hotel");
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
}
