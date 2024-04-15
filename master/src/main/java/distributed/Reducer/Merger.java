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
        // ArrayList<HashMap<String, Integer>> mergedMaps = new ArrayList<>();
        
        Object mergedContents = null;

        for (Mail mail : receivedMails){
            
            if(mail.getSubject().equals("Book")){
                String[] contents = (String[]) mail.getContents();
                if( Boolean.parseBoolean(contents[0])){
                    String[] res = new String[]{mail.getSender(), contents[0], contents[1], contents[2]};
                    // System.out.println(mail.getSender());
                    mergedContents = res;
                }

            } else if (mail.getRecipient().contains("client")){

                for (Room contents : (ArrayList<Room>) mail.getContents()) {
                    mergedList.add(contents);
                }

                mergedContents = mergedList.clone();
            } else if (mail.getRecipient().equals("manager")){
                TreeMap<String, Long> mergedMap = new TreeMap<String, Long>();
                TreeMap<String, Long> map = (TreeMap<String, Long>) mail.getContents();

                map.forEach((key, value) -> {
                    if(mergedMap.get(key) == null){
                        mergedMap.put(key, value);
                    } else {
                        mergedMap.put(key, mergedMap.get(key) + value);
                    }
                });


                mergedMap.forEach((key, value) -> {System.out.println(key + ": " + value);});
                mergedContents = mergedMap.clone();
            }
                
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
