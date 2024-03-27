package distributed;

import java.io.IOException;

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
            testNewCapabilites();
        }

        Terminal term = new Terminal();
        term.setup();
        term.init();
    }

    public static void testNewCapabilites() {
        Server server = new Server();
        try {
            server.start(4555);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
