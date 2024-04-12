package distributed.Reducer;

public class ReducerApp {
    public static void main(String[] args) {
        System.out.println("Hello reducer");
        ReducerServer reducerServer = new ReducerServer();
        reducerServer.run();

    }
    
}
