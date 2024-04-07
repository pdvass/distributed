package distributed.Share;

import java.io.Serializable;

public class Tuple implements Serializable{
    private static final long serialVersionUID = 040420242020L;

    private String firstElement;
    private Object secondElement;

    public Tuple(String first, Object second){
        this.firstElement = first;
        this.secondElement = second;
    }

    public String getFirst(){
        return this.firstElement;
    }

    public Object getSecond(){
        return this.secondElement;
    }
}
