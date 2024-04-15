package distributed;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Entry point for the Worker.
 * 
 */
public class WorkerApp 
{
    public static void main( String[] args ) throws UnknownHostException, IOException
    {        
        System.out.println( "Hello, I am Worker" );

        Worker worker = new Worker();

        Thread workerThread = new Thread(worker);
        workerThread.start();
    }
}
