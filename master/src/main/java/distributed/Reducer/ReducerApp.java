package distributed.Reducer;

public class ReducerApp {
    public static void main(String[] args) {
        System.out.println("Hello, I am Reducer");
        
        ReducerServer reducerServer = new ReducerServer();
        reducerServer.run();

    }
    
}
