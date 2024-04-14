package distributed;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {
        if(args.length == 1 && args[0].equals("debug")){
            testNewCapabilities();
        }
        
        ClientTerminal cTerm = new ClientTerminal();
        cTerm.run();

        System.out.println("Hello, I am Client");
    }

    public static void testNewCapabilities(){
        // Filter f = new Filter("region:Kallithea", "dates:[21/04/2024, 23/04/2024]");
        // f.getDateRangeString();
        System.exit(0);
    }
}
