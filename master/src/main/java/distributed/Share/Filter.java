package distributed.Share;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import distributed.Estate.Hotel;
import distributed.Estate.Room;
import distributed.JSONFileSystem.JSONDirManager;

public class Filter implements Serializable {
    private static final long serialVersionUID = 290320241224L;

    private String region = "";
    private Date[] dateRange = new Date[2];
    private float stars = -1;
    private int nOfPersons = -1;
    
    public Filter(String[] filters){
        try {
            dateRange[0] = new SimpleDateFormat("dd/MM/yyyy").parse("12/12/9999");
            dateRange[1] = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2002");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(String filter : filters ){
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


    ////////////// UNIQUE TO SERVER //////////////
    public final List<Hotel> applyFilter(){
        JSONDirManager manager = new JSONDirManager();
        ArrayList<Hotel> hotels = new ArrayList<>();
        try {
            hotels = manager.getHotels();
        } catch (FileNotFoundException e) {
            System.out.println("No hotels yet, try adding some first. Returning empty list.");
            return hotels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Hotel> filteredHotels = hotels.stream()
                        .filter(hotel -> (hotel.getRegion().equals(this.region) || this.region.equals("")))
                        .filter(hotel -> (hotel.getStars() >= this.stars || this.stars == -1))
                        .collect(Collectors.toList());

        filteredHotels.iterator().forEachRemaining(
            hotel -> {
                ArrayList<Room> rooms = hotel.getRooms().stream()
                                    .filter(room -> (
                                        (    
                                             room.getStartDate().before(this.dateRange[0])  
                                          || room.getStartDate().equals(this.dateRange[0])
                                        )
                                          && room.getEndDate().after(this.dateRange[1])
                                        ))
                                    .filter(room -> (room.getNOfPeople() == this.nOfPersons || this.nOfPersons == -1))
                                    .collect(Collectors.toCollection(ArrayList::new));
                hotel.setHotelRoom(rooms);
            }
        );

        filteredHotels = filteredHotels.stream()
                                    .filter(hotel -> hotel.getRooms().size() != 0)
                                    .collect(Collectors.toList());

        return filteredHotels;
    }
    ////////////// UNIQUE TO SERVER //////////////
}
