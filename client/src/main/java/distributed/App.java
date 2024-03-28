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
        ClientTerminal cTerm = new ClientTerminal();
        cTerm.run();
    }
}
