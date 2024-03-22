import java.net.*;
import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) throws IOException {
        // Define server address and port
        String serverHostname = "192.168.2.29";
        int portNumber = 3389;

        try (
            // Create socket to connect to server
            Socket socket = new Socket(serverHostname, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                // Send data to server
                out.println(userInput);
                // Receive response from server
                System.out.println("Server response: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
            System.exit(1);
        }
    }
}