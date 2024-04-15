package distributed.Reducer;

import java.io.IOException;
import java.util.ArrayList;
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
public class Merger {
    private static volatile ArrayList<Mail> receivedMails = null;
    private static volatile boolean write_lock = true;
    private ReducerClient rClient = null;
    private Mail sendMail = null;


    public Merger(){
        if(receivedMails == null){ 
            receivedMails = new ArrayList<>(); 
        }
    }

    /**
     * This method receives the mails from all the workers synchronized
     * and calls the merger to merge their contents .
     * 
     * @param mail The object sent from the workers  
     * 
     */
    
    public void receiveMail(Mail mail){
        synchronized(receivedMails){
            while(!write_lock){
                try {
                    receivedMails.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            write_lock = false;

            receivedMails.add(mail);
            if (receivedMails.size() == ReducerHandler.totalHandlers){
                this.mergeContents();
                receivedMails.clear();
            }

            write_lock = true; 
            receivedMails.notifyAll();
        }
    }

    /**
     * Merges the contents each Worker Handler in Reducer's side leaves.
     * In case a client is the recipient then the merge is about ArrayLists
     * In case of a Manager's request, then it merges HashMaps.
     * 
     * @see ReducerHandler
     * 
     */

    @SuppressWarnings("unchecked")
    private void mergeContents(){
        
        ArrayList<Room> mergedList = new ArrayList<>();
        TreeMap<String, Long> mergedMaps = new TreeMap<>();
        String[] res = null;
        String subject = "";
        String recipient = "";
        
        Object mergedContents = null;

        for (Mail mail : receivedMails){
            
            if(mail.getSubject().equals("Book")){
                subject = "Book";
                String[] contents = (String[]) mail.getContents();
                if( Boolean.parseBoolean(contents[0])){
                    res = new String[]{mail.getSender(), contents[0], contents[1], contents[2]};
                    // System.out.println(mail.getSender());
                }
                
            } else if (mail.getRecipient().contains("client")){
                recipient = mail.getRecipient();
                for (Room contents : (ArrayList<Room>) mail.getContents()) {
                    mergedList.add(contents);
                }
                
            } else if (mail.getRecipient().equals("manager")){
                recipient = "manager";
                // TreeMap<String, Long> mergedMap = new TreeMap<String, Long>();
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
            // mergedMaps.forEach((key, value) -> {System.out.println(key + ": " + value);});
            mergedContents = mergedMaps.clone();
        } else {
            System.err.println("Problem");
        }

        System.out.println(receivedMails.get(0).getSubject());
        this.sendMail = new Mail(receivedMails.get(0).getSender(), receivedMails.get(0).getRecipient(), 
                                receivedMails.get(0).getSubject(), mergedContents);

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
        this.rClient = new ReducerClient();
        try {
            this.rClient.startConnection("localhost", 4555);
            this.rClient.sendMessage("reducer connection");
            String response = this.rClient.receiveMsg();

            if(!response.equals("reducer connected")){
                System.out.println("Could not connect");
            } 
            System.out.println("connected");
            this.rClient.sendObject(mail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
