package distributed;

import java.util.HashMap;
import java.util.Map;
import distributed.Estate.Room;

public class User {

    String name;
    String surname;
    String userId;
    Map<String, Room> reservations;

    public User(String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.reservations = new HashMap<>();
    }

    public void addReservation(String dateRange, Room room) {
        // map.put(dateRange,room);
    }

    //set , get για name surname userID ,reservations
}
