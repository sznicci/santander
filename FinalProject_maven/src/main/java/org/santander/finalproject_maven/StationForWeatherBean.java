import org.joda.time.LocalTime;

import java.util.Date;

public class StationForWeatherBean {

    private Integer bike_id;
    private Integer station_id;
    private Date date;
    private LocalTime time;
    private Integer month;
    private Integer day;
    private Integer hour;


    public Integer getBike_id() {
        return bike_id;
    }

    public void setBike_id(Integer bike_id) {
        this.bike_id = bike_id;
    }

    public Integer getStation_id() {
        return station_id;
    }

    public void setStation_id(Integer station_id) {
        this.station_id = station_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return bike_id + " " + station_id + " " + date + " " + time + " " + month + " " + day + " " + hour;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
