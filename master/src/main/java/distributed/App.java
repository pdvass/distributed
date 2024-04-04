package distributed;
/**
 * Project's entry point.
 *
 */
public class App 
{
   public static void main( String[] args )
    {

        Terminal term = new Terminal();
        Thread termThread = new Thread(term);

        termThread.start();

    }
}
