package distributed.Parser;

import java.io.FileNotFoundException;
import java.io.FileReader;

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

}
