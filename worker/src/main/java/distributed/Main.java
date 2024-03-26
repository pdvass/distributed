// package distributed;

// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.io.*;
// import java.net.*;

// public class Main {
//     public static void main(String[] args) {        
//             // Create a fixed-size thread pool with  threads
//             ExecutorService threadPool = Executors.newFixedThreadPool();
    
//             // Submit tasks to the thread pool
//             for (int i = 0; i < 10; i++) {
//                 final int taskNumber = i;
//                 threadPool.submit(() -> {
//                     System.out.println("Task " + taskNumber + " executed by " + Thread.currentThread().getName());
//                 });
//             }
//     }
// }