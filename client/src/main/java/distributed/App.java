package distributed;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Client's entry point
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {
        ClientTerminal clientTerm = new ClientTerminal();
        clientTerm.run();
    }

}
