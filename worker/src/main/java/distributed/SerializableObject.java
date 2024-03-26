package distributed;
import java.io.Serializable;

public class SerializableObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private Counter counter;

    public SerializableObject(Counter counter) {
        this.counter = counter;
    }

    public Counter getValue() {
        return counter;
    }

    @Override
    public String toString() {
        return "SerializableObject{" +
                "message='" + counter.getValue() + '\'' +
                '}';
    }
}

