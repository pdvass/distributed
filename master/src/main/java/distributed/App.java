package distributed;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import distributed.Estate.Room;

/**
 * Project's entry point.
 *
 */
public class App 
{
   public static void main( String[] args )
    {
        if(args.length == 1 && args[0].equals("debug")){
            // testNewCapabilites();
            Bookkeeper bookkeeper = new Bookkeeper();
            bookkeeper.createWorkers();
        }

        // Server server = new Server("My first thread");
        // server.run();
        // server.start();

        // //TerminalThreadPool threadPool = new TerminalThreadPool();

        // while(server.isAlive()){

        //     System.out.println("Waiting...");
        // }
    }

    public static void testNewCapabilites(){
        @SuppressWarnings("unused")
        String id = "";
        int value = 0;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest("FourSeasonsRoom1".getBytes(StandardCharsets.UTF_8));
            value += new BigInteger(hash).intValue();
            id = new String(hash, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
        }

        System.out.println("Hello");
        Room room = new Room("FourSeasonsRoom1", "21/04/2024", "30/04/2024");
        System.out.println(room.getIntId());
        System.out.println(value);

    }
}
