package distributed.JSONFileSystem;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONFileParser {
    private String path = "src/main/java/distributed/data/";

    /**
     * Constructor that uses a path given by the user. The file must be a 
     * JSON file.
     * @param path String representing the path of the file.
     */
    public JSONFileParser(String path){
        this.path += path;
    }

    /**
     * A setter for the path. It is named "update" becasue it
     * updates the internal state of the path variable. It should
     * point to a json file.
     * @param path String representing the new path of the file.
     */
    public void updatePath(String path){
        this.path = this.path.replaceAll("/\\w+.json", "/" + path + ".json");
    }

    /**
     * Parses the file located at the path given. May throw an exception.
     * @return A JSONObject representing the data of the JSON file given.
     * @throws Exception If there is a problem reading the file.
     */
    public JSONObject parseFile() throws FileNotFoundException, Exception{
        JSONObject data = null;
        JSONParser parser = new JSONParser();
        // FileReader may throw an Exception.
        data = (JSONObject) parser.parse(new FileReader(this.path));

        return data;
    }

    @SuppressWarnings("unchecked")
    public void iterateJSON(JSONObject data){
        if(data == null){
            throw new IllegalArgumentException("data argument should not be null") ;
        }
        if(data.size() == 0){
            return;
        }

        // Wrapper to save all the info for each hotel in the enclosing scopes.
        // We use it to avoid the following error:
        //  Local variable hi defined in an enclosing scope must be final or effectively final. [Java(536871575)]
        // The work around is by mutating each value instead of initializing.
        // This Struct-like represantation helps with keeping in mind the respective Classes implementations.
        var hotelWrapper = new Object(){
            ArrayList<String> id = new ArrayList<>();
            ArrayList<String> startDate = new ArrayList<>();
            ArrayList<String> endDate = new ArrayList<>();
            String value = ""; 
            String region = ""; 
            int rooms = 0;
        };
        
        // During these consecutive iterations every "get" function corresponds
        // to a key in the respective JSON file, represented by the path variable.
        // Every JSON file has the same structure. The iterations take advantage 
        // that the project uses a HashMap implementation.
        // reference: https://github.com/fangyidong/json-simple/blob/master/src/main/java/org/json/simple/JSONObject.java
        data.values().iterator().forEachRemaining(rooms -> {
            JSONObject roomsArray = (JSONObject) rooms;

            hotelWrapper.value = roomsArray.get("name").toString();                 
            hotelWrapper.region  = roomsArray.get("region").toString();

            // Rooms are represented internally by an ArrayList. We take advantage of it 
            // by representing rooms as an array in JSON's file structure.
            // reference: https://github.com/fangyidong/json-simple/blob/master/src/main/java/org/json/simple/JSONArray.java
            ((JSONArray) roomsArray.get("rooms")).iterator().forEachRemaining(room -> {
                JSONObject roomInfo = (JSONObject) room;
                roomInfo.values().iterator().forEachRemaining(info -> {

                    hotelWrapper.id.add(((JSONObject) info).get("id").toString());
                    hotelWrapper.startDate.add(((JSONObject) info).get("startDate").toString());
                    hotelWrapper.endDate.add(((JSONObject) info).get("endDate").toString());
                    
                });
                hotelWrapper.rooms++;
            });
        });


        // Should be converted to logs.
        System.out.println("Hotel " + hotelWrapper.value + " in region " + hotelWrapper.region + " is Done");
        System.out.println("It has " + hotelWrapper.rooms + " rooms. Its " + 
                        (hotelWrapper.rooms == 1 ? "room is " : "rooms are ") + 
                "available from " +  hotelWrapper.startDate.toString() + " to " + hotelWrapper.endDate.toString() +
                ". The respective ids are " + hotelWrapper.id.toString());
    }
}
