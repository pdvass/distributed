package distributed;

import java.io.*;
import java.net.*;
//import java.util.Scanner;

/**
 * @author stellagianno
 */

public class TCPServer {
    public TCPServer(){

    }

    public static class DeserializationUtil {
        public DeserializationUtil(){
            
        }

        public static Object deserializeObject(byte[] data) throws IOException, ClassNotFoundException, Exception {
            ObjectInputStream ois = null;
            try 
            {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bis);
            } catch (IOException e){
                e.printStackTrace();
                System.exit(-1);
            }
            if(ois != null){
                return ois.readObject();
            } else {
                throw new Exception("Problem parsing ois");
            }
        }
    }

    public void run() throws Exception {
        // Define port to listen on
        int portNumber = 3389;
        // Create server socket
        try 
        { ServerSocket serverSocket = new ServerSocket(portNumber);
          System.out.println("Server is listening...");

            // Accept incoming connections
            try 
            {
            Socket clientSocket = serverSocket.accept();
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Connected to client.");
          
            // Read input object from client
            // Receive the serialized object from the client
            byte[] serializedData = (byte[]) in.readObject();
            System.out.println("Received object from client: " );
            
            // Deserialize the object
            Object receivedObject = DeserializationUtil.deserializeObject(serializedData);
            System.out.println("Received object class: " + receivedObject.getClass().getName());
            
            // Process the received object
            if (receivedObject instanceof Counter) {
                Counter receivedCounter = (Counter) receivedObject;
                receivedCounter.increment(); // Call the increment method

                // Send the updated Counter object back to the client
                out.writeObject(receivedCounter);
                out.flush(); 

                System.out.println("Counter incremented and sent back to client.");
            } 
            else{
                System.out.println("wrong");
                System.out.println("Received object class: " + receivedObject.getClass().getName());
            }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}