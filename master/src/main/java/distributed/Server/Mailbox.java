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
public class Mailbox extends Thread {
    private static volatile HashMap<HandlerTypes, ArrayList<Tuple>> messages = null;
    // For bigger app, every type of user, would have its own message queue, or even 
    // multiple per type.
    // private static volatile HashMap<String, ArrayList<Tuple>> clientMessages = null;
    // private static volatile ArrayList<String> managerMessages                 = null;
    // private static volatile HashMap<String, ArrayList<Tuple>> workerMessages = null;
    private JSONDirManager manager = new JSONDirManager();
    private static volatile boolean write_lock = true;
    private static volatile boolean read_lock = true;

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

    // protected ArrayList<Tuple> checkClientMessages(String clientId){
    //     synchronized (clientMessages){
    //         @SuppressWarnings("unchecked")
    //         ArrayList<Tuple> msgs = (ArrayList<Tuple>) clientMessages.get(clientId).clone();
    //         clientMessages.get(clientId).clear();
    //         return msgs;
    //     }

    // }

    /**
     * Check the shared space for any new messages.
     * 
     * @param type Type of handler trying to access the mail.
     * @return The mails directed to the handler.
     */
    protected  ArrayList<Tuple> checkMail(HandlerTypes type){
        synchronized (messages){
            while(!read_lock){
                try {
                    messages.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            read_lock = false;
            @SuppressWarnings("unchecked")
            ArrayList<Tuple> mails = (ArrayList<Tuple>) messages.get(type).clone();
            messages.get(type).clear();
            read_lock = true;
            messages.notifyAll();
            return mails;
        }
        
    }

    /**
     * Add mail to the space refering to a specific type of Handler.
     * 
     * @param type The type of Handler 
     * @param message The message for the handler.
     */
    protected void addMessage(HandlerTypes fromType, HandlerTypes toType, String message, Object contents){
        synchronized (messages){
            while(!write_lock){
                try {
                    messages.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            write_lock = false;
            // this.read_lock = false;
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
                    }
                    break;
                case "Filter":
                    t = new Tuple(message, contents);
                    messages.get(toType).add(t);
                    if(toType.equals(HandlerTypes.WORKER)){
                        System.out.println("Added a filter for worker");
                        System.out.println(messages.get(HandlerTypes.WORKER).size());
                    }
                    break;
                case "Transaction":
                    manager.logTransaction((String) contents);
                default:
                    break;
            }
            write_lock = true;
            // this.read_lock = true;
            messages.notifyAll();
            return;
        }
    }
}
