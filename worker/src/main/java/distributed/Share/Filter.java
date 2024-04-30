package distributed.Share;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import distributed.Estate.Room;

/**
 * Filter is used to create predicates that hotels must fulfill to qualify for
 * sending them back to the client.
 * 
 * @author pdvass
 */
public class Filter implements Serializable {

    private static final long serialVersionUID = 290320241224L;
    private String region = "";
    private Date[] dateRange = new Date[2];
    private float stars = -1;
    private int nOfPersons = -1;
    
    public Filter(String[] filters){
        try {
            // Infinity-like parameters to initialise the dateRange array. If dates are given as
            // a filter, then they are "relaxed".
            dateRange[0] = new SimpleDateFormat("dd/MM/yyyy").parse("12/12/9999");
            dateRange[1] = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2002");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(String filter : filters ) {
            if (filter.contains("region")) {
                String[] reg = filter.trim().split(":");
                this.region = reg[1];
            }

            if(filter.contains("dates")){
                String[] dates = filter.split(":")[1]
                                    .replace("[", "")
                                    .replace("]", "")
                                    .split("-");
                try {
                    Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(dates[0]);
                    Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(dates[1]);
                    this.dateRange[0] = startDate;
                    this.dateRange[1] = endDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if(filter.contains("stars")){
                this.stars = Float.parseFloat(filter.split(":")[1]);
            }

            if(filter.contains("nOfPersons")){
                this.nOfPersons = Integer.parseInt(filter.split(":")[1]);
            }
        }
    }

    ////////////////////////// GETTERS //////////////////////////

    public String getRegion(){
        return this.region;
    }

    public Date[] getDateRange(){
        return this.dateRange;
    }

    public float getStars(){
        return this.stars;
    }

    public int getNOfPersons(){
        return this.nOfPersons;
    }

    public String getDateRangeString(){
        return this.dateRange[0].toString() + " and " + this.dateRange[1].toString();
    }

    ////////////////// GETTERS //////////////////
    ////////////// UNIQUE TO WORKER /////////////

    /**
     * Applies filters to Room List.
     * 
     * @return A list of all the rooms.
     * 
     * @see Room
     */
    public final List<Room> applyFilter(List<Room> hotels){
        // We use a more fuctional way of creating list, by initializing a stable state hotel list
        // and creating a new one first by which hotel satisfies the filter predicates, if there are any.
        List<Room> filteredHotels = hotels.stream()
                        .filter(hotel -> hotel.getHotelsRegion().equals(this.region) || this.region.equals(""))
                        .filter(hotel -> hotel.getHotelsStars() >= this.stars || this.stars == -1)
                        .filter(room -> ( room.getStartDate().before(this.dateRange[1])  
                                         ||  room.getStartDate().equals(this.dateRange[1]) ))
                        .filter(room -> room.getEndDate().after(this.dateRange[0]) )
                        .filter(room -> room.getNOfPeople() == this.nOfPersons || this.nOfPersons == -1)
                        .collect(Collectors.toList());

        // System.out.println(this.getDateRangeString());
        // System.out.println(this.dateRange[0].toString() + " " + this.dateRange[1].toString());
        // hotels.stream().forEach(room -> {
        //     System.out.println(room.getStartDate().toString() + " before " + this.dateRange[0].toString());
        //     System.out.println(room.getStartDate().before(this.dateRange[0])  
        //                     ||  room.getStartDate().equals(this.dateRange[0]) );
        // });
        return filteredHotels;
    }

    public final List<Room> applyManagerFilter(List<Room> hotels){
        // We use a more fuctional way of creating list, by initializing a stable state hotel list
        // and creating a new one first by which hotel satisfies the filter predicates, if there are any.
        List<Room> filteredHotels = hotels.stream()
                        .filter(hotel -> hotel.getHotelsRegion().equals(this.region) || this.region.equals(""))
                        .filter(hotel -> hotel.getHotelsStars() >= this.stars || this.stars == -1)
                        .filter(room -> ( room.getStartDate().after(this.dateRange[0])
                                         ||  room.getStartDate().equals(this.dateRange[0]) ))
                        .filter(room -> room.getEndDate().before(this.dateRange[1])
                                        ||  room.getEndDate().equals(this.dateRange[1]))
                        .filter(room -> room.getNOfPeople() == this.nOfPersons || this.nOfPersons == -1)
                        .collect(Collectors.toList());

        // System.out.println(this.getDateRangeString());
        // System.out.println(this.dateRange[0].toString() + " " + this.dateRange[1].toString());
        // hotels.stream().forEach(room -> {
        //     System.out.println(room.getStartDate().toString() + " before " + this.dateRange[0].toString());
        //     System.out.println(room.getStartDate().before(this.dateRange[0])  
        //                     ||  room.getStartDate().equals(this.dateRange[0]) );
        // });
        return filteredHotels;
    }

}
