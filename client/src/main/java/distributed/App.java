package distributed;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Entry point for the Client.
 * 
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {
        
        ClientTerminal cTerm = new ClientTerminal();
        cTerm.run();

        System.out.println("Hello, I am Client");
    }

}
