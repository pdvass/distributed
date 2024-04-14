package distributed.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
     *  This method merges the contents 
     * 
     */

    @SuppressWarnings("unchecked")
    private void mergeContents(){
        
        // System.out.println("Number of mails received: " + receivedMails.size());
        
        ArrayList<Room> mergedList = new ArrayList<>();
        ArrayList<HashMap<String, Integer>> mergedMaps = new ArrayList<>();
        
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
                for (HashMap<String, Integer> contents : (ArrayList<HashMap<String, Integer>>) mail.getContents()) {
                    mergedMaps.add(contents);
                }

                for (int i = 0; i < mergedMaps.size() - 1; i++) {
                    mergedMaps.get(i+1).forEach((key, value) -> mergedMaps.get(0).merge(key, value, (v1, v2) -> v1.equals(v2) ? v1: v1 + v2));
                }

                mergedContents = mergedMaps.get(0).clone();
            }
                
        } 

        this.sendMail = new Mail(receivedMails.get(0).getSender(), receivedMails.get(0).getRecipient(), 
                                receivedMails.get(0).getSubject(), mergedContents);

        this.sendMailToServer(this.sendMail);

    }
    
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
