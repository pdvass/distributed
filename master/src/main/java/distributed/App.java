package distributed;

import distributed.JSONFileSystem.JSONDirManager;
import distributed.Server.Server;

/**
 * Project's entry point.
 *
 */
public class App 
{
   public static void main( String[] args )
    {
        if(args.length == 1 && args[0].equals("debug")){
            testNewCapabilties();
        }

        Terminal term = new Terminal();
        // term.setup();
        // term.init();

        Thread termThread = new Thread(term);
        Server server = new Server();
        Thread serverThread = new Thread(server);

        termThread.start();
        serverThread.start();

        System.out.println("Hello");
    }

    public static void testNewCapabilties() {
        JSONDirManager manager = new JSONDirManager();
        Terminal term = new Terminal();
        term.setup();
        term.init();

        try {
            manager.getHotels();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.exit(0);

    }
}
