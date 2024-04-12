package distributed;

public class ReducerApp {
    public static void main(String[] args) {
        System.out.println("Hello reducer");
        ReducerServer reducerServer = new ReducerServer();
        reducerServer.run();

        ReducerClient reducerClient = new ReducerClient();
        reducerClient.startConnection("localhost",4555);
        clientSocket.sendObject(getMail());

    }
    
}
