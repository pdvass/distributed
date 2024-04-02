package distributed.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class Mailbox {
    private static volatile HashMap<String, ArrayList<String>> messages       = null;
    // For bigger app, every type of user, would have its own message queue, or even 
    // multiple per type.
    // private static volatile HashMap<String, ArrayList<String>> clientMessages = null;
    // private static volatile ArrayList<String> managerMessages                 = null;
    // private static volatile HashMap<String, ArrayList<String>> workerMessages = null;

    public Mailbox(){
        if(messages == null){
            messages = new HashMap<>();
            messages.put("Manager", new ArrayList<>());
            messages.put("Worker", new ArrayList<>());
            messages.put("Client", new ArrayList<>());
        }
    }

    protected ArrayList<String> checkMail(String type){
        @SuppressWarnings("unchecked")
        ArrayList<String> mails = (ArrayList<String>) messages.get(type).clone();
        messages.get(type).clear();
        return mails;
    }

    protected synchronized void addMessage(String type, String message){
        messages.get(type).add(message);
        return;
    }



}
