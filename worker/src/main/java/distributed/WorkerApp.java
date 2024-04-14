package distributed;

import java.io.*;


public class WorkerApp {
    public static void main(String[ ] args) throws IOException{
        System.out.println( "Hello maybe it must deleted" );

        Worker worker = new Worker();

        Thread workerThread = new Thread(worker);
        workerThread.start();
    }
}
