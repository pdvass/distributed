package distributed;
import java.io.Serializable;

public class Counter implements Serializable {
    private int counter;
    public Counter(int n){
        this.counter= n;
    }
    public void increment(){
        counter++;
    }
    public int getValue(){
        return counter;
    }
    public static void main(String[] args){
        Counter counter = new Counter(1);
        int value = counter.getValue(); 
        System.out.println(value);
        counter.increment();
        int valuenext = counter.getValue();
        System.out.println(valuenext);
    }
}

