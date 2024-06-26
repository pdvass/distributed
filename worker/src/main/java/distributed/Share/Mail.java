package distributed.Share;

import java.io.Serializable;

/**
 * Mail represents the main way that the server components are communicating with each other.
 * It holds information for the sender, the recipient, the subject and the contents that 
 * are the data, each component should work on. Mail are left and picked up in the Mailbox.
 * 
 * @author pdvass
 */
public class Mail implements Serializable {
    
    private static final long serialVersionUID = 040420242020L;
    private String sender;
    private String recipient;
    private String subject;
    private Object contents;

    /**
     * Constructs the mail
     * 
     * @param sender String representing the ID of the sender.
     * @param recipient String representing the ID of the recipient.
     * @param subject String denoting the subject of the mail.
     * @param contents Object with the contents of the Mail.
     */
    public Mail(String sender, String recipient, String subject, Object contents){
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.contents = contents;
    }

    public String getRecipient(){
        return this.recipient;
    }

    public String getSender(){
        return this.sender;
    }

    public String getSubject(){
        return this.subject;
    }

    public Object getContents(){
        return this.contents;
    }

    public void setContents(Object newContents){
        this.contents = newContents;
    }

    /**
     * Swaps the sender with the recipient.
     */
    public void respond(){
        String temp = this.sender;
        this.sender = this.recipient;
        this.recipient = temp;
    }
}
