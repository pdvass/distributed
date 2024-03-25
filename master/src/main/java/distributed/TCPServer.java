import java.net.*;
import java.io.*;

public class TCPServer {
    public static void main(String[] args) throws IOException {
        // Define port to listen on
        int portNumber = 3389;

        // Create server socket
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening...");

            // Accept incoming connections
            try (Socket clientSocket = serverSocket.accept();
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                System.out.println("Connected to client.");

                // Receive data from client and echo it back
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    out.println(inputLine);
                }
            }

        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}