package distributed.JSONFileSystem;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    /**
     * Empty constructor that reads all the JSON files on the path folder. 
     * Can be extended with DFS to organise the folder with subfolders.
     */
    public JSONDirManager(){
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
    public void addHotel(String name, String region){
        name = name.replaceAll(" ", "");
        String fileName = this.path + name + ".json";
        try {
            File newHotel = new File(fileName);
            if(newHotel.createNewFile()){
                System.out.println("New Hotel Added");
            } else {
                System.out.println("File Already exists");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JSONFileParser parser = new JSONFileParser(fileName);

        parser.createHotelJSON(name, region);

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
    public void addRoom(String name, String startDate, String endDate){
        // Duplicate with 135 - 145, might need to extract function.
        name = name.replaceAll(" ", "");
        String fileName = this.path + name + ".json";
        JSONFileParser parser = new JSONFileParser(fileName);
        JSONObject data = null;
        try {
            data = parser.parseFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        JSONArray rooms = (JSONArray) ((JSONObject) data.get(name)).get("rooms");
        JSONObject room = new JSONObject();
        room.put("id", name + "Room" + Integer.toString(rooms.size() + 1));
        room.put("startDate", startDate);
        room.put("endDate", endDate);

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }    
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
    }

    // NOTE: To be implemented
    public void printAllHotels(){}
    public void addReview(){}
}
