import java.net.*;
import java.io.*;

public class TCPServer {
    public static void main(String[] args) throws IOException{

        String serverHostName = "localhost";
        int portNumber = 3389;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening...");

            try (Socket clientSocket = serverSocket.accept();
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                System.out.println("Connected to client.");

                try (Socket serverBSocket = new Socket(serverHostName, portNumber);
                
                     ObjectOutputStream serverBOut = new ObjectOutputStream(serverBSocket.getOutputStream())) {
                    serverBOut.writeObject(objToSend);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}