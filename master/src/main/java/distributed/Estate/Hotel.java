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

    // NOTE: Should implement
    public void book(){
        // NOTE: Should be synchronized

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
