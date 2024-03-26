package distributed;

import distributed.JSONFileSystem.JSONDirManager;

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
        JSONDirManager manager = new JSONDirManager();
        manager.printAllHotels();
    }
}
