package distributed;

import java.io.*;


public class WorkerApp {
    public static void main(String[ ] args) throws IOException{
        Worker worker = new Worker();
        worker.start();
 
    }
}
