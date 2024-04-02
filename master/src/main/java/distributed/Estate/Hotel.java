package distributed.Estate;

import java.util.Date;
import java.util.ArrayList;

/**
 * Hotel class is the main way to translate JSON files into in memory objects
 * and export Room capabilities. It, also, exports an API for client to use.
 * 
 * @author pdvass
 * @see Room
 */

public class Hotel {
    private ArrayList<Room> rooms;
    private int stars;
    private int nOfReviews;
    private String region;
    private String name;

    public Hotel(String name, String region, int stars, int nOfReviews){
        this.name = name;
        this.region = region;
        this.stars = stars;
        this.nOfReviews = nOfReviews;
        this.rooms = new ArrayList<Room>();
    }

    public ArrayList<Boolean> checkAvailabilty(String username, Date from, Date to){
        ArrayList<Boolean> availabity = new ArrayList<>();
        for(Room room : rooms){
            availabity.add(room.isAvailable(from, to));
        }
        return availabity;
    }

    /**
     * Finds the room that is available on the dates desired by the user.
     * @param username representing the name of the user who wants to make the reservation.
     * @param from Date representing the first day of which the room need to be booked.
     * @param to Date representing the last day of which the room need to be booked.
     */
    public void book(String username, Date from, Date to) {
        // NOTE: Should be synchronized
        for(Room room : rooms) {
            if(room.isAvailable(from, to)) {
                room.book(from, to);
                System.out.println("Room " + room.getName() + " booked for user " + username + " from " + from.toString() + " to " + to.toString());
                return; // Booked the first available room and exit
            }
        }
        System.out.println("No available rooms for the specified dates.");

    }

    public ArrayList<Room> getRooms(){
        return this.rooms;
    }

    public int getStars(){
        return this.stars;
    }

    public int getNOfReviews(){
        return this.nOfReviews;
    }

    public String getRegion(){
        return this.region;
    }

    public String getName(){
        return this.name;
    }
    
}
