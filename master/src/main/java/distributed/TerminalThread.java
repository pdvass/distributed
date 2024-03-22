
public class TerminalThread extends Thread{

    private String argument;
    private Terminal terminal;
	
	public TerminalThread(String argument){
		this.argument = argument;
	}

	@Override
	public void run(){

        Terminal term = new Terminal();
        term.setup();
        term.init();

		System.out.println("Thread with id: "+ threadId() + " started!");
		
		try {
			sleep(5000);
			System.out.println("Argument: "+argument);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Thread with id: "+ threadId() + " exiting");
		
	}
    
}