package distributed;

import distributed.JSONFileSystem.JSONDirManager;
import distributed.Share.Request;
import distributed.Share.Filter;
import distributed.Share.Mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.regex.Matcher;
/**
 * Terminal to parse user input and invoke the methods that are needed.
 * @author pdvass
 */
public class Terminal extends Thread {
    private Socket serverConn = null;
    private Request req = null;
    /**
     * Text printed when the "list" command is given
    */
    private String listText = 
            "Available commands:\n" +
            "- list: Lists Available commands\n" +
            "- help (h): Prints help text\n" +
            "- hotels: Lists all hotels with all the rooms they have.\n" +
            "- add: Adds hotel or room to existing database.\n" +
            "- remove: Removes hotel or room from existing databse.\n" +
            "- book: Books a room from a hotel to a given date range.\n";

    public Terminal(){}

    public void run(){
        this.setup();
        this.init();
    }

    /**
     * Sets up ant dependencies or setting in the system.
     */
    public void setup(){
        System.out.println("Setting up the system...");
        try {
            this.serverConn = new Socket("localhost", 4555);
            this.req = new Request(this.serverConn, "Manager connection");
            this.req.sendMessage();
            if(this.req.receiveMessage().equals("manager connected")){
                System.out.println("Connected to server.");
            } else {
                System.out.println("Not connected");
            }

        } catch (IOException e) {
            System.out.println("Could not connect to server");
        }
        System.out.println("Everything is ready.");
    }

    /**
     * Initiates the terminal to read from Master any commands
     * @throws ClassNotFoundException 
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
                    manager.printAllHotels();
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
                case "show":
                    System.out.println("Show booking applying to the Filter");
                    Filter f = new Filter(commandTokens);
                    try {
                        this.req.changeContents("show");
                        this.req.sendMessage();
                        this.req.changeContents(f);
                        this.req.sendRequestObject();
                        HashMap<String, Long> answer = null;
                        try {
                            answer = (HashMap<String, Long>) this.req.receiveRequestObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        answer.forEach((key, value) -> {System.out.println(key + ": " + value);});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    break;
                case "users":
                    this.req.changeContents("users");
                    try {
                        this.req.sendMessage();
                        int num = (int) this.req.receiveRequestObject();
                        System.out.println(num);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case "check":
                    this.req.changeContents("check");
                    try {
                        this.req.sendMessage();
                        Mail msg = (Mail) this.req.receiveRequestObject();
                        while(!(msg.getSubject().equals("Message") && msg.getContents().equals("-1"))){
                            if(msg.getSubject().equals("Message")){
                                System.out.println(msg.getContents());
                            }
                            msg = (Mail) this.req.receiveRequestObject();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    if(commandTokens[0].isEmpty()){
                        System.out.println("");
                    } else {
                        System.out.printf("Command \"%s\" is unknown. Please try again.\n", in);
                        this.levenshtein(commandTokens[0]);
                    }
                    break;
            }
        }
        input.close();
        System.exit(0);
    }

    private void levenshtein(String given){
        final String[] commands = new String[]{"quit", "help", "hotels", "add", "remove", "list", "book", "show"};
        LevenshteinDistance dist = new LevenshteinDistance();
        ArrayList<Integer> distances = new ArrayList<>();
        for(String command : commands){
            distances.add(dist.apply(given, command));
        }
        int positionOfMinDistance = distances.indexOf(Collections.min(distances));
        System.out.printf("Hint: Did you mean %s?\n", commands[positionOfMinDistance]);

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
        if(tokens.length < 2){
            System.err.println("Not enough arguments");
            return;
        }
        switch (tokens[1].toLowerCase()) {
            case "hotel":
                try {
                    ArrayList<String> hotelInfo = this.getCommand("add", "hotel", in);
                    String region = hotelInfo.get(1).replace(')', ' ').trim();
                    if(region.trim().length() == 0){
                        System.err.println("Region is empty.");
                        return;
                    }
                    float stars = Float.parseFloat(hotelInfo.get(2));
                    if(stars > 5.0f){
                        System.err.println("Stars cannot exceed 5.0");
                        return;
                    }
                    int nOfReviews = Integer.parseInt(hotelInfo.get(3));
                    if(nOfReviews < 0){
                        System.err.println("Can't have negative number of reviews");
                        return;
                    }
                    manager.addHotel(hotelInfo.get(0), region, stars, nOfReviews);
                    System.out.printf("Added hotel %s located at %s.\n", 
                                        hotelInfo.get(0), region);

                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Not enough arguments for hotel. Type \"help add\", to see the syntax.");
                } catch (NumberFormatException e){
                    System.err.println("Either Available Rooms or Price cannot be parsed. Available rooms must be a parseable integer");
                    System.err.println("Price must be a parseable float. Try removing any currency signs.");
                } catch (Exception e){
                    manager.logError(e.getMessage());
                }
                break;

            case "room":
                try {
                    ArrayList<String> hotelInfo = this.getCommand("add", "room", in);
                    Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(hotelInfo.get(1));
                    Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(hotelInfo.get(2));
                    if(startDate.after(endDate)){
                        System.err.println("Start date must be before endDate");
                        return;
                    }
                    float cost = Float.parseFloat(hotelInfo.get(3));
                    int nOfPeople = Integer.parseInt(hotelInfo.getLast());
                    manager.addRoom(hotelInfo.get(0), hotelInfo.get(1), hotelInfo.get(2), cost, nOfPeople);
                    System.out.printf("Added to hotel %s date range %s to %s. It costs %.2f$ and it is for %d %s\n",
                                hotelInfo.get(0), startDate.toString(),
                                endDate.toString(), cost, nOfPeople,
                                (nOfPeople > 1) ? "people" : "person");
                    
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Not enough arguments for hotel. Type \"help add\", to see the syntax.");
                } catch (Exception e){
                    manager.logError(e.getMessage());
                }
                break;
            case "review":
                manager.addReview("Hotel California", 5);
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
        if(tokens.length < 3){
            System.err.println("Not enough arguments");
            return;
        }
        ArrayList<String> hotelInfo = new ArrayList<>();
        switch (tokens[1]) {
            case "hotel":
                try{
                    hotelInfo = this.getCommand("remove", "hotel", in);
                    manager.removeHotel(hotelInfo.get(0));
                } catch (IndexOutOfBoundsException e){
                    System.err.println("You must give the name of the hotel which you need to remove.");
                } catch (Exception e){
                    manager.logError(e.getMessage());
                }
                break;
            case "room":
                try {
                    hotelInfo = this.getCommand("remove", "room", in);
                    int roomId = Integer.parseInt(hotelInfo.getFirst());
                    manager.removeRoom(hotelInfo.getLast(), roomId);
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Not enough arguments. Type \"help remove\", to see th syntax.");
                } catch (NumberFormatException e){
                    System.err.println("RoomID must be a parseable integer");
                } catch (Exception e){
                    manager.logError(e.getMessage());
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
                System.out.println("\t~> add hotel $HOTEL_NAME at $REGION ($STARS $nOfReviews)");
                System.out.println("For room: ");
                System.out.println("\t~> add room to $HOTEL_NAME from $START_DATE to $END_DATE ($COST $nOfPersons)");
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
                System.out.println("\t~> book room $ID from $HOTEL_NAME from $START_DATE to $END_DATE");
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
    private ArrayList<String> getCommand(String action, String object, String in) throws Exception{
        // To further understand the regex expressions used for each action and object
        // see analysis on the right side of
        // https://regex101.com/
        String regex = "";
        ArrayList<String> hotelInfo = new ArrayList<>();
        Pattern pattern;
        Matcher matcher;
        switch (action) {
            case "add":
                switch (object) {
                    case "room":
                        //?<somename> in regex denotes a group with name: somename.
                        regex = "add\\s+room\\s+to\\s+(?<name>.+)\\s+from\\s+(?<startdate>\\d{2}/\\d{2}/\\d{4})\\s+to\\s+(?<enddate>\\d{2}/\\d{2}/\\d{4})\\s+\\((?<price>\\d+.?\\d*)\\s+(?<nofpersons>\\d+)\\)";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(in);
                        if(matcher.find()){
                            hotelInfo.add(matcher.group("name"));
                            hotelInfo.add(matcher.group("startdate"));
                            hotelInfo.add(matcher.group("enddate"));
                            hotelInfo.add(matcher.group("price"));
                            hotelInfo.add(matcher.group("nofpersons"));
                        } else {
                            throw new Exception("Could not configure either hotel name or info.");
                        }
                        break;
                    case "hotel":
                        regex = "(?<cmd>add hotel)\\s+(?<name>.+)\\s+at\\s+(?<region>.+)\\s+\\((?<stars>\\d+.?\\d*)\\s+(?<nofreviews>\\d+)\\)";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(in);
                        if(matcher.find()){
                            hotelInfo.add(matcher.group("name"));
                            hotelInfo.add(matcher.group("region"));
                            hotelInfo.add(matcher.group("stars"));
                            hotelInfo.add(matcher.group("nofreviews"));
                        } else {
                            throw new Exception("Could not configure either hotel name or info.");
                        }
                        break;
                    default:
                        break;
                }
                break;
            case "remove":
                switch (object) {
                    case "room":
                        regex = "remove\\s+room\\s+(?<id>\\d+)\\s+from\\s+(?<name>.+)\\s*";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(in);
                        if(matcher.find()){
                            hotelInfo.add(matcher.group("id"));
                            hotelInfo.add(matcher.group("name"));
                        } else {
                            throw new Exception("Could noy configure if or name");
                        }
                        break;
                    case "hotel":
                        regex = "(?<cmd>remove hotel)\\s+(?<name>.+)\\s*";
                        pattern = Pattern.compile(regex);
                        matcher = pattern.matcher(in);
                        if(matcher.find()){
                            hotelInfo.add(matcher.group("name"));
                        } else {
                            throw new Exception("Could not configure hotel name");
                        }
                        break;
                    default:
                        break;
                }
                break;
                default:
                    break;
            }
        return hotelInfo;
    }
}
