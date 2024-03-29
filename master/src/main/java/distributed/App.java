package distributed;

import java.io.IOException;
import java.util.List;
import distributed.Estate.Hotel;

import distributed.Server.Server;
import distributed.Share.Filter;

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
        term.setup();
        term.init();
    }

    public static void testNewCapabilties() {
        Server server = new Server();
        try {
            server.start(4555);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // Filter filter = new Filter(new String[]{"dates:[19/04/2024-24/04/2024]"});
        // List<Hotel>  f = filter.applyFilter();
        // f.forEach(hotel -> System.out.println(hotel.toString()));

        System.exit(0);

    }
}
