package distributed;

import org.json.simple.parser.*;

import java.io.FileReader;

import org.json.simple.*;;
/**
 * Hello world!
 *
 */
public class App 
{
   public static void main( String[] args )
    {
        testNewCapabilites();

        Terminal term = new Terminal();
        term.setup();
        term.init();
    }

    public static void testNewCapabilites(){
        try {
            JSONParser parser = new JSONParser();
            JSONObject data = (JSONObject) parser.parse(new FileReader("src/main/java/distributed/data/initialData.json"));
            System.out.println(( (JSONObject) data.get("hotels")).get("Four Seasons").toString());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            System.out.println("CWD: " + System.getProperty("user.dir"));
        }

        System.out.println("Hello");
    
    }

}
