package distributed.JSONFileSystem;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import distributed.Estate.Hotel;

/**
 * JSONFileParser responsible for parsing JSON files that follows the structure
 * corresponding for hotel and room management. It is used as a tool by JSONDirManager.
 * 
 * @author pdvass
 * @see JSONDirManager
 */
public class JSONFileParser {

    private String path;

    /**
     * Constructor that uses a path given by the user. The file must be a 
     * JSON file.
     * @param path String representing the path of the file.
     */
    // NOTE: this should either throw or create the file if it does not exist.
    public JSONFileParser(String path){
        this.path = path;
    }

    /**
     * A setter for the path. It is named "update" becasue it
     * updates the internal state of the path variable. It should
     * point to a json file.
     * @param path String representing the new path of the file.
     */
    protected void updatePath(String path){
        this.path = this.path.replaceAll("/\\w+.json", "/" + path + ".json");
    }

    /**
     * Parses the file located at the path given. May throw an exception.
     * 
     * @return A JSONObject representing the data of the JSON file given.
     * 
     * @throws FileNotFoundException If the file does not exist.
     * @throws Exception If there is a problem reading the file.
     */
    protected JSONObject parseFile() throws FileNotFoundException, Exception{
        JSONObject data = null;
        JSONParser parser = new JSONParser();

        data = (JSONObject) parser.parse(new FileReader(this.path));
        return data;
    }

    @SuppressWarnings("unchecked")
    protected Hotel iterateJSON(JSONObject data){

        if(data == null){
            throw new IllegalArgumentException("data argument should not be null") ;
        }

        if(data.size() == 0){
            return null;
        }

        // Wrapper to save all the info for each hotel in the enclosing scopes.
        // We use it to avoid the following error:
        // - Local variable hi defined in an enclosing scope must be final or effectively final. [Java(536871575)]
        // The work around is by mutating each value instead of initializing.
        // This Struct-like represantation helps with keeping in mind the respective Classes implementations.
        var hotelWrapper = new Object(){
            String name = ""; 
            // Start of Room Info
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> startDate = new ArrayList<>();
            ArrayList<String> endDate = new ArrayList<>();

            float cost = 0;
            int nOfPeople = 0;
            // End of Room Info
            int rooms = 0;
            String region = "";
            float stars = 0.0f;
            int nOfReviews = 0;
        };

        var returnValue = new Object(){
            Hotel hotel = null;
        };
        
        // During these consecutive iterations every "get" function corresponds
        // to a key in the respective JSON file, represented by the path variable.
        // Every JSON file has the same structure. The iterations take advantage 
        // that the project uses a HashMap implementation.
        // reference: https://github.com/fangyidong/json-simple/blob/master/src/main/java/org/json/simple/JSONObject.java
        data.values().iterator().forEachRemaining(rooms -> {
            JSONObject roomsArray = (JSONObject) rooms;

            hotelWrapper.name = roomsArray.get("name").toString();                 
            hotelWrapper.region  = roomsArray.get("region").toString();
            hotelWrapper.stars = Float.parseFloat(roomsArray.get("stars").toString());
            hotelWrapper.nOfReviews = Integer.parseInt(roomsArray.get("nOfReviews").toString());

            returnValue.hotel = new Hotel(hotelWrapper.name, hotelWrapper.region, hotelWrapper.stars, hotelWrapper.nOfReviews);

            // Rooms are represented internally by an ArrayList. We take advantage of it 
            // by representing rooms as an array in JSON's file structure.
            // reference: https://github.com/fangyidong/json-simple/blob/master/src/main/java/org/json/simple/JSONArray.java
            ((JSONArray) roomsArray.get("rooms")).iterator().forEachRemaining(room -> {
                JSONObject roomInfo = (JSONObject) room;
                roomInfo.values().iterator().forEachRemaining(info -> {

                    hotelWrapper.id.add(((JSONObject) info).get("id").toString());
                    hotelWrapper.startDate.add(((JSONObject) info).get("startDate").toString());
                    hotelWrapper.endDate.add(((JSONObject) info).get("endDate").toString());
                    hotelWrapper.cost = Float.parseFloat(((JSONObject) info).get("cost").toString());
                    hotelWrapper.nOfPeople = Integer.parseInt(((JSONObject) info).get("nOfPeople").toString());

                    returnValue.hotel.addRoom(hotelWrapper.id.get(hotelWrapper.rooms), hotelWrapper.startDate.get(hotelWrapper.rooms), 
                        hotelWrapper.endDate.get(hotelWrapper.rooms), hotelWrapper.cost, hotelWrapper.nOfPeople);

                    
                });
                hotelWrapper.rooms++;
            });
        });


        // Should be converted to logs.
        // System.out.println("Hotel " + hotelWrapper.name + " in region " + hotelWrapper.region + " is Done");
        // System.out.println("It has " + hotelWrapper.rooms + " rooms. Its " + 
        //                 (hotelWrapper.rooms == 1 ? "room is " : "rooms are ") + 
        //         "available from " +  hotelWrapper.startDate.toString() + " to " + hotelWrapper.endDate.toString() +
        //         ". The respective ids are " + hotelWrapper.id.toString());

        return returnValue.hotel;
    }

    @SuppressWarnings("unchecked")
    protected void createHotelJSON(String name, String region, float stars, int n){

        JSONObject data = new JSONObject();
        data.put("region", region);
        data.put("name", name);

        JSONArray rooms = new JSONArray();
        data.put("rooms", rooms);
        data.put("stars", stars);
        data.put("nOfReviews", n);

        JSONObject hotel = new JSONObject();
        hotel.put(name, data);
        
        String json = hotel.toJSONString();

        try {
            FileWriter writer = new FileWriter(this.path);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
