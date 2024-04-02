package distributed;

// import java.util.concurrent.TimeUnit;
//import distributed.JSONFileSystem.JSONDirManager;

/**
 * Project's entry point.
 *
 */
public class App 
{
   public static void main( String[] args ) throws Exception
    {

        Terminal term = new Terminal();
        Thread termThread = new Thread(term);

        termThread.start();
    }
}
