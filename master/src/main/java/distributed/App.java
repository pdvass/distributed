package distributed;

// import java.util.concurrent.TimeUnit;
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

    public static void testNewCapabilites(){
        System.out.println("Hello");
        JSONDirManager manager = new JSONDirManager();
        manager.addHotel("Hotel Victory", "Viktoria");

        manager.addRoom("Hotel Victory", "19/05/2024", "29/05/2024");

        // try{
        //     TimeUnit.SECONDS.sleep(5);
        // } catch (Exception e){
        //     System.out.println(e.getMessage());
        // }

        // manager.removeRoom("Hotel Victory", 2);

    }
}
