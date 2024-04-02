package distributed;


/**
 * The Server class will represent the single-threaded nature of the Terminal class.
 * 
 * @author panagou
 * @see Terminal
 */

public class Server extends Thread {

    private Terminal terminal;
	
	public Server() {}

	@Override
	public void run() {

        terminal = new Terminal();
        terminal.setup();
        terminal.init();
		
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
    
}