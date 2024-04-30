package distributed;

import java.io.*;

/**
 * Worker's entry point
 *
 */
public class WorkerApp 
{
    public static void main(String[ ] args) throws IOException
    {
        Worker worker = new Worker();
        worker.start();
    }

}
