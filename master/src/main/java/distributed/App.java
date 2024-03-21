package distributed;
import distributed.JSONFileSystem.JSONFileParser;

import org.json.simple.*;
/**
 * Project's entry point.
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

        JSONFileParser parser = new JSONFileParser("FourSeasons.json");
        JSONObject data = null;
        try {
            data = parser.parseFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        parser.iterateJSON(data);
        parser.updatePath("Pergamos");
        try {
            data = parser.parseFile();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        parser.iterateJSON(data);

    }

}
