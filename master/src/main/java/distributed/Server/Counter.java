package distributed.Server;

import java.io.Serializable;

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
