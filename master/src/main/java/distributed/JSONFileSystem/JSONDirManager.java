package distributed.JSONFileSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import distributed.Estate.Hotel;

/**
 * JSONDirManager (JSON Directory Manager) is responsible for creating, 
 * deleting and updating JSON files representing hotels.
 * 
 * @author pdvass
 */
public class JSONDirManager {
    // NOTE: Change path to also correspond to test path.
    private final String path = "src/main/java/distributed/data/";
    private ArrayList<File> fileList = new ArrayList<>();
    private Logger logger;

    /**
     * Empty constructor that reads all the JSON files on the path folder. 
     * Can be extended with DFS to organise the folder with subfolders.
     */
    public JSONDirManager(){
        logger = new Logger();
        File files = new File(this.path);
        for(File file : files.listFiles()){
            if(!file.isDirectory() && getFileExtension(file).equals("json")){
                this.fileList.add(file);
            }
        }
    }

    private String getFileExtension(File file){
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        return extension;
    }

    /**
     * Uses JSONFileParser as a tool to create a new, almost empty, JSON file
     * which represents the new Hotel. Room array is initially empty.
     * 
     * @param name Name of the hotel
     * @param region Region on which the hotel is located.
     * 
     * @see JSONFileParser#createHotelJSON(String, String)
     */
    public void addHotel(String name, String region, float stars, int n){
        name = name.replaceAll(" ", "");
        String fileName = this.path + name + ".json";
        try {
            File newHotel = new File(fileName);
            if(newHotel.createNewFile()){
                System.out.println("New Hotel Added");
                fileList.add(newHotel);
            } else {
                System.out.println("File Already exists");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JSONFileParser parser = new JSONFileParser(fileName);

        parser.createHotelJSON(name, region, stars, n);
        
        // Write to Log
        String contents = String.format("Added hotel %s", name);
        logger.setLevel("info");
        logger.writeToLog(contents);

    }

    /**
     * Adds room to a Room array of an already existing hotel between a date range.
     * 
     * @param name Name of the hotel of which the room is going to be added.
     * @param startDate String in the format "dd/MM/yyyy" representing the first day
     * the hotel is available
     * @param endDate String in the format "dd/MM/yyyy" representing the last day
     * the hotel is available
     */
    @SuppressWarnings("unchecked")
    public void addRoom(String name, String startDate, String endDate, float cost, int nOfPeople){
        // Duplicate with 135 - 145, might need to extract function.
        name = name.replaceAll(" ", "");
        String fileName = this.path + name + ".json";
        JSONFileParser parser = new JSONFileParser(fileName);
        JSONObject data = null;
        try {
            data = parser.parseFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        JSONArray rooms = (JSONArray) ((JSONObject) data.get(name)).get("rooms");
        JSONObject room = new JSONObject();
        JSONObject roomInfo = new JSONObject();
        String roomID = name + "Room" + Integer.toString(rooms.size() + 1);
        roomInfo.put("id", roomID);
        roomInfo.put("startDate", startDate);
        roomInfo.put("endDate", endDate);
        roomInfo.put("cost", cost);
        roomInfo.put("nOfPeople", nOfPeople);

        room.put("room" + Integer.toString(rooms.size() + 1), roomInfo);
        rooms.add(room);

        String json = data.toJSONString();
        // Rewrite the whole folder with the new room added.
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String contents = String.format("Added room %s to hotel %s", roomID, name);
        logger.setLevel("info");
        logger.writeToLog(contents);
    }

    /**
     * Removes a hotel by deleting its corresponding JSON file.
     * @param name The name of hotel that is going to be deleeted.
     */
    public void removeHotel(String name){
        name = name.replaceAll(" ", "");
        String fileName = this.path + name + ".json";    
        try {
            File newHotel = new File(fileName);
            if(newHotel.delete()){
                System.out.println("Hotel deleted");
            }
            this.fileList.stream()
                         .filter(file -> file.getName().equals(newHotel.getName()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String contents = String.format("Removed Hotel %s", name);
        logger.setLevel("warn");
        logger.writeToLog(contents);
    }

    /**
     * Removes a room from the room array of a hotel.
     * 
     * @param name The name of the hotel
     * @param roomNumber The room number that is going to be deleted. 
     */
    @SuppressWarnings("unchecked")
    public void removeRoom(String name, int roomNumber){
        name = name.replaceAll(" ", "");
        String fileName = this.path + name + ".json";
        JSONFileParser parser = new JSONFileParser(fileName);
        JSONObject data = null;
        try {
            data = parser.parseFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        JSONArray rooms = (JSONArray) ((JSONObject) data.get(name)).get("rooms");
        final String enclosingName = name;
        rooms.removeIf(room -> {
            return ((JSONObject) room).get("id").equals(enclosingName + "Room" + Integer.toString(roomNumber));
        });

        data.remove("rooms");

        String json = data.toJSONString();

        // Rewrite the whole folder with the room removed.
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String contents = String.format("Removed room %d from hotel %s", roomNumber, name);
        logger.setLevel("warn");
        logger.writeToLog(contents);
    }

    /**
     * Get all hotels that are saved in {@link #fileList} at the moment this
     * method is beign invoked.
     * 
     * @return ArrayList with all the hotels manager is directing.
     * @throws FileNotFoundException
     * @throws Exception
     */
    public ArrayList<Hotel> getHotels() throws FileNotFoundException, Exception {
        ArrayList<Hotel> hotels = new ArrayList<>();
        for(File f : fileList){
            JSONFileParser parser = new JSONFileParser(this.path + f.getName());
            JSONObject data = parser.parseFile();
            hotels.add(parser.iterateJSON(data));
        }
        return hotels;
    }

    public void logError(String contents){
        logger.setLevel("danger");
        logger.writeToLog("Error occured during master working time: " + contents);
    }

    public void printAllHotels(){
        try {
            for(Hotel hotel : this.getHotels()){
                System.out.println(hotel.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // NOTE: To be implemented
    public void addReview(){}
}
