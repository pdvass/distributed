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
    private float stars;
    private int nOfReviews;
    private String region;
    private String name;

    public Hotel(String name, String region, float stars, int nOfReviews){
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
     * Create a room object and add it to rooms.
     * 
     * @param name Name of the hotel for creating the ID
     * @param startDate Date first being available
     * @param endDate Date last being available
     * @param cost Cost per night
     * @param nOfPeople Max people
     * 
     * @see Hotel#rooms
     */
    public void addRoom(String name, String startDate, String endDate, float cost, int nOfPeople){
        Room room = new Room(name, startDate, endDate, cost, nOfPeople);
        this.rooms.add(room);
    }

    // NOTE: Should implement
    public void book(){
        // NOTE: Should be synchronized

    }

    public ArrayList<Room> getRooms(){
        return this.rooms;
    }

    public float getStars(){
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

    public void setHotelRoom(ArrayList<Room> rooms){
        this.rooms = rooms;
    }

    public String toString(){
        // NOTE: StringBuilder is more efficient than String concats
        String repr = String.format("Name: %s. It is located in %s and averages %.2f stars from %d reviews. ", this.name, this.region, this.stars, this.nOfReviews);
        repr += "Here is some info for its room" + (this.rooms.size() > 1 ? "s:\n" : ":\n");
        for(Room room : this.rooms){
            repr += String.format("- %s for %d people. It costs %.2f and it is available from %s to %s.\n", room.getName(), room.getNOfPeople(), room.getCost(), 
                                        room.getStartDate(), room.getEndDate());
        }
        if(this.rooms.isEmpty()){
            repr += "It has no rooms yet.";
        }
        return repr;
    }
    
}
