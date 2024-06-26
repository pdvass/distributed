package distributed.Estate;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;
import java.util.TreeMap;

import java.nio.charset.StandardCharsets;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * The room class mainly needed for keeping information about when a room object is 
 * booked. Also needed for computing the hash. Each room belongs to a Hotel object and cannot exist externally.
 * 
 * @author pdvass
 * @see Hotel
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 80420241743L;
    private String name;
    private byte[] id;
    private Date startDate;
    private Date endDate;
    private int nOfPeople;
    private float cost;
    private TreeMap<LocalDate, Integer> rangeMap;

    // Variables to use for providing workers with hotels' info
    // Should not be used in master.
    @SuppressWarnings("unused")
    private long totalBookings;
    protected float hotelsStars;
    @SuppressWarnings("unused")
    private String hotelsRegion;
    
    

    public Room(String name, String startDate, String endDate, float cost, int nOfPeople, String hotelsReg, float hotelsStars){

        this.name = name;
        // Create hash from the JSON's name that has been assigned to the room of the hotel.
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            this.id = digest.digest(name.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
        }

        try {
            this.startDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
            this.endDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        
        List<LocalDate> range = this.produceDateRange(this.startDate, this.endDate);
        this.rangeMap = new TreeMap<LocalDate,Integer>();

        // Iterate the list and use each date as the key for the TreeMap.
        range.stream().forEach(i -> this.rangeMap.put(i, 0));

        this.nOfPeople = nOfPeople;
        this.cost = cost;
        this.hotelsRegion = hotelsReg;
        this.hotelsStars = hotelsStars;
        this.totalBookings = 0;
    }

    /**
     * Books the room by mutating the internal state of its available dates list. The range is
     * inclusive - exclusive.
     * 
     * @param from Date representing the first day of which the room need to be booked.
     * @param to Date representing the last day of which the room need to be booked. This day is not
     * considered booked by the room.
     */
    public void book(Date from, Date to){
        // NOTE: Should be synchronized
        List<LocalDate> range = this.produceDateRange(from, to);
        range.stream().forEach(date -> this.rangeMap.put(date, this.rangeMap.get(date) + 1));
    }

    /**
     * Shows to the hotel - owner object if the room is available all the days a date range.
     * @param from Date representing the first day of which the room needs to be checked.
     * @param to Date representing the last day of which the room needs to be checked.
     * @return True if the room is available false otherwise.
     */
    protected boolean isAvailable(Date from, Date to){
        List<LocalDate> range = this.produceDateRange(from, to);
        
        boolean testAnyMatch = range.stream().anyMatch(date -> this.rangeMap.get(date) == 1);
        System.out.println(testAnyMatch);
        return !testAnyMatch;
    }

    /**
     * Internal tool for producing a {@link List} with all the dates between the
     * date range given, as its values.
     * 
     * @param from Date representing the first day the range.
     * @param to Date representing the last day the range. It is not added to the list.
     * 
     * @return A list with all the Dates ranging between first and last date as {@link LocalDate}s
     * 
     * @see Room#book(Date, Date)
     * @see Room#Room(String, String, String)
     */
    private List<LocalDate> produceDateRange(Date from, Date to){
        // Create DateRange from startDate to endDate (inclusive - exclusive)
        LocalDate fromLocal = from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocal = to.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // function between only works with Temporal eg LocalDate.
        long numOfDaysBetween = ChronoUnit.DAYS.between(fromLocal, toLocal);
        List<LocalDate> range = IntStream.iterate(0, i -> i + 1)
                        .limit(numOfDaysBetween)
                        .mapToObj(i -> fromLocal.plusDays(i))
                        .collect(Collectors.toList());
        
        return range;
    }

    /**
     * Getter for the hash of the room's id as a String.
     * 
     * @return String of the hash.
     */
    public String getId(){
        return new String(this.id, StandardCharsets.UTF_8);
    }

    /**
     * Getter for the hash of the room's id as an int. Useful if combined with 
     * modulo operation determine which worker should have the room.
     * 
     * @return Integer of the hash.
     */
    public int getIntId(){
        return new BigInteger(this.id).intValue();
    }

    public Date getStartDate(){
        return this.startDate;
    }

    public Date getEndDate(){
        return this.endDate;
    }

    public int getNOfPeople(){
        return this.nOfPeople;
    }

    public String getName(){
        return this.name;
    }

    public float getCost(){
        return this.cost;
    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();
        String hotelName = this.name.replaceFirst("Room\\d", "");
        // https://www.regular-expressions.info/unicode.html
        // Link to show how it works:
        //  https://regex101.com/r/QsUvXF/1
        hotelName = String.join(" ", hotelName.split("(?=\\p{Lu})"));

        String intro = String.format("\u2022 Room %s belongs to hotel \"%s\". ", this.name, hotelName);
        sb.append(intro);

        String info = String.format("It costs %.2f per night and it is available from %tD to %tD. It can host up to %d people.\n",  
                            this.cost, this.startDate, this.endDate, this.nOfPeople );
        sb.append(info);

        String bookInfo = String.format("  To book it enter code %d with the date range you want to book it.\n", this.getIntId());
        sb.append(bookInfo);

        return sb.toString();
    }

}
