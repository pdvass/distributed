package distributed;


import distributed.Share.*;

import java.io.*;
import java.net.*;
import java.io.Serializable;
import java.util.Scanner;

import distributed.Estate.Hotel;
import distributed.Estate.Room;

public class WorkerApp {
    public static void main(String[ ] args) throws IOException{

        Scanner myObj = new Scanner(System.in);
        String workerName = myObj.nextLine();
        Worker worker = new Worker(workerName);
        worker.startWorker();

        
        //while(true){}
 
    }
}
