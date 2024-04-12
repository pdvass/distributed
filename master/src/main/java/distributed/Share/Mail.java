package distributed.Share;

import java.io.Serializable;

public class Mail implements Serializable{
    private static final long serialVersionUID = 040420242020L;

    private String sender;
    private String recipient;
    private String subject;
    private Object contents;

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

    public void respond(){
        String temp = this.sender;
        this.sender = this.recipient;
        this.recipient = temp;
    }

    public String toString(){
        return String.format("From %s to %s with subject %s", this.sender, this.recipient, this.subject);
    }
}
