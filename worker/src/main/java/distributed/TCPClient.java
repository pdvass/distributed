package distributed;
import java.io.*;
import java.net.*;
import java.io.Serializable;
import java.util.Scanner;



public class TCPClient {

    public class SerializationUtil {
        public  byte[] serializeObject(Serializable obj) throws IOException {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos))
             {
                oos.writeObject(obj);
                oos.flush();
                return bos.toByteArray();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // Define server address and port
        String serverHostname = "localhost";
        int portNumber = 3389;

        try { 
            // Create socket to connect to server
            Socket socket = new Socket(serverHostname, portNumber);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()) ;

            // Create and serialize the object to be sent
            Counter c = new Counter(1);
            SerializableObject objectToSend = new SerializableObject(c);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objectToSend);
            oos.flush();
            byte[] serializedObject = baos.toByteArray();
            //SerializableObject serializableObject = counter.toSerializableObject();

            // Send the object to the server
            out.writeObject(serializedObject);
            out.flush(); // Flush the output stream to ensure all data is sent

            System.out.println("Object sent to server.");

            while(true){}

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
                System.exit(1);
            }
    }
}
