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
        if(args.length == 1 && args[0].equals("debug")){
            testNewCapabilites();
        }

        Terminal term = new Terminal();
        term.setup();
        term.init();
        TCPServer server = new TCPServer();
        server.run();

    }

    public static void testNewCapabilites(){
        System.out.println("i am good");
    }
}
