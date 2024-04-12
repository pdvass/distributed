package distributed;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Room rooms; X
 * Tuple<Client, filter>
 * Tuple<Client, hotel>
 * Tuple<Manager, rangeDates>
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {        
        System.out.println( "Hello World!" );

        Worker worker = new Worker();

        Thread workerThread = new Thread(worker);
        workerThread.start();
    }
}
