package distributed.Share;

import java.io.Serializable;

/**
 * Simple Class example to exchange with the server.
 */
public class Counter implements Serializable {
    private int counter;

    public Counter(int counter){
        this.counter = counter;
    }

    public int getCounter(){
        return this.counter;
    }

    public void setCounter(int c){
        this.counter = c;
    }
}
