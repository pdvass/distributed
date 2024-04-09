package distributed.Server;

import java.util.ArrayList;
import java.util.HashMap;

import distributed.Share.Tuple;

import distributed.JSONFileSystem.JSONDirManager;

/**
 * Mailbox creates a shared among threads and objects space, for each handler 
 * to check in its own time.
 * 
 * @see ClientHandler
 * @see ManagerHandler
 * @see WorkerHandler
 * 
 * @author pdvass
 */
public class Mailbox {
    private static volatile HashMap<HandlerTypes, ArrayList<Tuple>> messages = null;
    // For bigger app, every type of user, would have its own message queue, or even 
    // multiple per type.
    // private static volatile HashMap<String, ArrayList<String>> clientMessages = null;
    // private static volatile ArrayList<String> managerMessages                 = null;
    // private static volatile HashMap<String, ArrayList<String>> workerMessages = null;
    private final static Object SHARED_LOCK = new Object();
    private JSONDirManager manager = new JSONDirManager();

    public Mailbox(){
        // Since the same mailbox can be used by every Handler, the first one to 
        // access this Constructor is responsible for creating the shared space.
        if(messages == null){
            messages = new HashMap<>();
            for(HandlerTypes type : HandlerTypes.values()){
                messages.put(type, new ArrayList<>());
            }
        }
    }

    /**
     * Check the shared space for any new messages.
     * 
     * @param type Type of handler trying to access the mail.
     * @return The mails directed to the handler.
     */
    protected ArrayList<Tuple> checkMail(HandlerTypes type){
        @SuppressWarnings("unchecked")
        ArrayList<Tuple> mails = (ArrayList<Tuple>) messages.get(type).clone();
        messages.get(type).clear();
        return mails;
    }

    /**
     * Add mail to the space refering to a specific type of Handler.
     * 
     * @param type The type of Handler 
     * @param message The message for the handler.
     */
    protected void addMessage(HandlerTypes fromType, HandlerTypes toType, String message, Object contents){
            synchronized (SHARED_LOCK){

                String log = String.format("%s left a message to %s", fromType.toString(), toType.toString());
                manager.logInfo(log);
                Tuple t = null;
                switch (message) {
                    case "Message":
                        t = new Tuple(message, contents);
                        messages.get(toType).add(t);
                        if(toType.equals(HandlerTypes.CLIENT)){
                            System.out.println("Added a message for client");
                            System.out.println(messages.get(HandlerTypes.CLIENT).size());
                            notify();
                        }
                        break;
                    case "Filter":
                        t = new Tuple(message, contents);
                        messages.get(toType).add(t);
                        if(toType.equals(HandlerTypes.WORKER)){
                            System.out.println("Added a filter for worker");
                            System.out.println(messages.get(HandlerTypes.WORKER).size());
                            notify();
                        }
                        break;
                    case "Transaction":
                        manager.logTransaction((String) contents);
                    default:
                        break;
                }
            }
        SHARED_LOCK.notifyAll();
        return;
    }

    protected Object getLock(){
        return SHARED_LOCK;
    }

}
