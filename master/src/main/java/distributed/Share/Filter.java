package distributed.Share;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Filter implements Serializable {
    private String region;
    private Date[] dateRange = new Date[2];
    private float stars;
    private byte nOfPersons;
    
    public Filter(String... filters){
        for(String filter : filters ){
            if (filter.contains("region")) {
                String[] reg = filter.split(":");
                this.region = reg[1];
            }

            if(filter.contains("dates")){
                String[] dates = filter.split(":")[1]
                                    .replace("[", "")
                                    .replace("]", "")
                                    .split(",");
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
                this.nOfPersons = Byte.parseByte(filter.split(":")[1]);
            }
        }
    }

    public String getRegion(){
        return this.region;
    }

    public Date[] getDateRange(){
        return this.dateRange;
    }

    public String getDateRangeString(){
        return this.dateRange[0].toString() + " and " + this.dateRange[1].toString();
    }
}
