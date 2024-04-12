package distributed.Server;

import java.util.ArrayList;
import java.util.HashMap;

import distributed.Share.Mail;

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
    private static volatile HashMap<HandlerTypes, ArrayList<Mail>> messages = null;
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

    /**
     * Check the shared space for any new messages.
     * 
     * @param type Type of handler trying to access the mail.
     * @return The mails directed to the handler.
     */
    protected  ArrayList<Mail> checkMail(HandlerTypes type, String callerID){
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
            ArrayList<Mail> mails = (ArrayList<Mail>) messages.get(type).clone();
            ArrayList<Mail> directedTo = new ArrayList<>();
            ArrayList<Mail> notDirectedTo = new ArrayList<>();
            // NOTE: MUST CHANGE 
            if(!mails.isEmpty() && type.equals(HandlerTypes.CLIENT)){
                for(Mail mail : mails){
                    if(mail.getRecipient().equals(callerID)){
                        directedTo.add(mail);
                    } else {
                        notDirectedTo.add(mail);
                    }
                    
                }
            } else {
                directedTo = mails;
            }
            messages.get(type).clear();
            messages.get(type).addAll(notDirectedTo);
            read_lock = true;
            messages.notifyAll();
            // return mails;
            return directedTo;
        }
    }

    /**
     * Add mail to the space refering to a specific type of Handler.
     * 
     * @param type The type of Handler 
     * @param message The message for the handler.
     */
    protected void addMessage(HandlerTypes fromType, HandlerTypes toType, Mail mail){
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
            switch (mail.getSubject()) {
                case "Message":
                case "Filter":
                    messages.get(toType).add(mail);
                    break;
                case "Transaction":
                    manager.logTransaction((String) mail.getContents());
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
