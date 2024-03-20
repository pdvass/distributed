package distributed;
import distributed.Parser.JSONFileParser;

import org.json.simple.*;
/**
 * Hello world!
 *
 */
public class App 
{
   public static void main( String[] args )
    {
        if(args.length == 1 && args[0].equals("debug")){
            testNewCapabilites();
        }

        Terminal term = new Terminal();
        term.setup();
        term.init();
    }

    public static void testNewCapabilites(){
        System.out.println("Hello");

        JSONFileParser parser = new JSONFileParser();
        JSONObject data = null;
        try {
            data = parser.parseFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        JSONArray hotels = (JSONArray) data.get("hotels");
        // System.out.println(data);
        parser.iterateJSON(hotels);
    }

}
