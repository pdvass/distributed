package distributed.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

import distributed.Estate.Room;
import distributed.Share.Mail;

/**
 * The Merger class receives the Objects from the workers and merges them. 
 * 
 * @see Mail
 * 
 * @author stellagianno
 * @author panagou
 * @author pdvass
 */
public class Merger extends Thread {

    private static volatile HashMap<Long, ArrayList<Mail>> receivedMails_ = null;
    private static volatile boolean write_lock = true;
    private ReducerClient reducerClient = null;
    private Mail sendMail = null;


    public Merger(){
        if(receivedMails_ == null){
            receivedMails_ = new HashMap<>();
        }
    }

    /**
     * This method receives the mails from all the workers synchronized and 
     * calls the merger to merge their contents.
     * 
     * @param mail The object sent from the workers.
     */
    @SuppressWarnings("unchecked")
    public void receiveMail(Mail mail){

        synchronized(receivedMails_) {
            while(!write_lock) {
                try {
                    receivedMails_.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            write_lock = false;
            if(mail == null){
                System.out.println("Worker Died");
            }
            if(receivedMails_.size() > 0 && receivedMails_.containsKey(mail.getTransactionNumber())){
                receivedMails_.get(mail.getTransactionNumber()).add(mail);
            } else {
                ArrayList<Mail> transactionMails = new ArrayList<>();
                transactionMails.add(mail);
                receivedMails_.put(mail.getTransactionNumber(), transactionMails);
            }

            Set<Long> keys = receivedMails_.keySet();
            for(long key: keys){
                if(receivedMails_.get(key).size() == 3){
                    ArrayList<Mail> mailToBeMerged = (ArrayList<Mail>) receivedMails_.get(key).clone();
                    receivedMails_.remove(key);
                    Runnable merger = () -> {this.mergeContents(mailToBeMerged);};
                    merger.run();
                }
            }

            write_lock = true; 
            receivedMails_.notifyAll();
        }
    }

    /**
     * Merges the contents each Worker Handler in Reducer's side leaves.
     * In case a client is the recipient then the merge is about ArrayLists
     * In case of a Manager's request, then it merges HashMaps.
     * 
     * @see ReducerHandler
     */
    @SuppressWarnings("unchecked")
    private void mergeContents(ArrayList<Mail> receivedMailsToMerge){
        
        ArrayList<Room> mergedList = new ArrayList<>();
        TreeMap<String, Long> mergedMaps = new TreeMap<>();

        String[] res = null;
        String subject = "";
        String recipient = "";

        Object mergedContents = null;

        for (Mail mail : receivedMailsToMerge) {
            
            if(mail.getSubject().equals("Book")){

                subject = "Book";
                String[] contents = (String[]) mail.getContents();
                if( Boolean.parseBoolean(contents[0])){
                    res = new String[]{mail.getSender(), contents[0], contents[1], contents[2]};
                }
                
            } else if (mail.getRecipient().contains("client")){

                recipient = mail.getRecipient();
                for (Room contents : (ArrayList<Room>) mail.getContents()) {
                    System.out.println(contents.toString());
                    mergedList.add(contents);
                }
                
            } else if (mail.getRecipient().equals("manager")){

                recipient = "manager";
                TreeMap<String, Long> map = (TreeMap<String, Long>) mail.getContents();
                
                map.forEach((key, value) -> {
                    if(mergedMaps.get(key) == null){
                        mergedMaps.put(key, value);
                    } else {
                        mergedMaps.put(key, mergedMaps.get(key) + value);
                    }
                });
                
            }
            
        } 
        
        if(subject.equals("Book")){
            mergedContents = res;
        } else if(recipient.contains("client")){
            mergedContents = mergedList.clone();
        } else if(recipient.equals("manager")){
            mergedContents = mergedMaps.clone();
        } else {
            System.err.println("Problem");
        }

        System.out.println(receivedMailsToMerge.get(0).getSubject());
        this.sendMail = new Mail(receivedMailsToMerge.get(0).getSender(), receivedMailsToMerge.get(0).getRecipient(), 
                                receivedMailsToMerge.get(0).getSubject(), mergedContents, -1);

        this.sendMailToServer(this.sendMail);

    }
    
    /**
     * Takes a mail with the merged contents and sends it back to the server.
     * 
     * @param mail the final mail.
     * 
     * @see ReducerClient
     */
    private void sendMailToServer(Mail mail){
        this.reducerClient = new ReducerClient();
        try {
            this.reducerClient.startConnection("localhost", 4555);
            this.reducerClient.sendMessage("reducer connection");

            String response = this.reducerClient.receiveMsg();
            if(!response.equals("reducer connected")){
                System.out.println("Could not connect");
            } 
            
            System.out.println("connected");
            this.reducerClient.sendObject(mail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
