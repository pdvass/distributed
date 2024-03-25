import java.net.*;
import java.io.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {
        // Define server address and port
        String serverHostname = "192.168.2.14";
        int portNumber = 3389;

        // try (
        //     // Create socket to connect to server
        //     Socket socket = new Socket(serverHostname, portNumber);
        //     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //     BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        // ) {
        //     String userInput;
        //     while ((userInput = stdIn.readLine()) != null) {
        //         // Send data to server
        //         out.println(userInput);
        //         // Receive response from server
        //         System.out.println("Server response: " + in.readLine());
        //     }
        // } catch (UnknownHostException e) {
        //     System.err.println("Unknown host: " + serverHostname);
        //     System.exit(1);
        // } catch (IOException e) {
        //     System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
        //     System.exit(1);
        // }

        try (
            // Create socket to connect to server
            Socket socket = new Socket(serverHostname, portNumber);) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()) ;

            // Create an object to send to the server
            Counter counter = new Counter(1); 
            SerializableObject serializableObject = counter.toSerializableObject();

            // Send the object to the server
            out.writeObject(serializableObject);
            out.flush(); // Flush the output stream to ensure all data is sent

            while ((userInput = stdIn.readLine()) != null) {
                        // Send data to server
                        out.println(userInput);
                        // Receive response from server
                        System.out.println("Server response: " + in.readNextLine());
            }
            System.out.println("Object sent to server.");

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
                System.exit(1);
            } catch (UnknownHostException e) {
                System.err.println("Unknown host: " + serverHostname);
                System.exit(1);
            } 
    }
}