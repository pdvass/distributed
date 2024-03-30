package distributed.Share;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    
    /**
     * Constructor of the filters. Takes advantage of specially formatted strings.
     * to create the filters.
     * 
     * @param filters A String array of specially formatted strings.
     */
    public Filter(String[] filters){
        try {
            // Infinity-like parameters to initialise the dateRange array. If dates are given as
            // a filter, then they are "relaxed".
            dateRange[0] = new SimpleDateFormat("dd/MM/yyyy").parse("12/12/9999");
            dateRange[1] = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2002");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for(String filter : filters ){
            if (filter.contains("region")) {
                String[] reg = filter.split(":");
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
}
