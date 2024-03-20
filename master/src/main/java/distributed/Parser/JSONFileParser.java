package distributed.Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONFileParser {
    private String path = "src/main/java/distributed/data/initialData.json";

    /**
     * Empty Constructor. Uses internal path variable.
     */
    public JSONFileParser(){}

    /**
     * Constructor that uses a path given by the user. The file must be a 
     * JSON file.
     * @param path String representing the path of the file.
     */
    public JSONFileParser(String path){
        this.path = path;
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
    public void iterateJSON(JSONArray data){
        if(data == null){
            throw new IllegalArgumentException("data argument should not be null") ;
        }
        if(data.size() == 0){
            return;
        }

        data.iterator().forEachRemaining( elem -> {
            JSONObject e = (JSONObject) elem;
            // Wrapper to save all the info for each hotel in the enclosing scopes.
            var hotelWrapper = new Object(){
                ArrayList<String> id = new ArrayList<>();
                ArrayList<String> startDate = new ArrayList<>();
                ArrayList<String> endDate = new ArrayList<>();
                String value = ""; 
                String region = ""; 
                int rooms = 0;
            };
            
            e.values().iterator().forEachRemaining(rooms -> {
                JSONObject roomsArray = (JSONObject) rooms;

                hotelWrapper.value = roomsArray.get("name").toString();                 
                hotelWrapper.region  = roomsArray.get("region").toString();

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

            System.out.println("Hotel " + hotelWrapper.value + " in region " + hotelWrapper.region + " is Done");
            System.out.println("It has " + hotelWrapper.rooms + " rooms. Its " + 
                            (hotelWrapper.rooms == 1 ? "room is " : "rooms are " ) + 
                    "available from " +  hotelWrapper.startDate.toString() + " to " + hotelWrapper.endDate.toString() +
                    ". The respective ids are " + hotelWrapper.id.toString());
        });

    }

}
