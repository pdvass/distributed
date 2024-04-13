package distributed;

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

        System.out.println("Hello, I am Manager");
    }
}
