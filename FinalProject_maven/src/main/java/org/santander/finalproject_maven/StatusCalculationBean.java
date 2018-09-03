import org.joda.time.LocalTime;

import java.sql.Time;
import java.util.Date;

public class StatusCalculationBean {

    private Integer station_id;
    private Date date;
    private Time time;
    private Integer capacity;
    private Integer takeouts;
    private Integer returns;
    private Integer available;
    private Integer t_sum;
    private Integer r_sum;
    private Integer status_takeouts;
    private Integer status_returns;
    private String temp_max;
    private Double temp_max_raw;
    private String temp_min;
    private Double temp_min_raw;
    private String rain;
    private Double rain_raw;
    private Integer wind_speed_knots;


    public Integer getStation_id() {
        return station_id;
    }

    public void setStation_id(Integer station_id) {
        this.station_id = station_id;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public Double getTemp_max_raw() {
        return temp_max_raw;
    }

    public void setTemp_max_raw(Double temp_max_raw) {
        this.temp_max_raw = temp_max_raw;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public Double getTemp_min_raw() {
        return temp_min_raw;
    }

    public void setTemp_min_raw(Double temp_min_raw) {
        this.temp_min_raw = temp_min_raw;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public Double getRain_raw() {
        return rain_raw;
    }

    public void setRain_raw(Double rain_raw) {
        this.rain_raw = rain_raw;
    }

    public Integer getWind_speed_knots() {
        return wind_speed_knots;
    }

    public void setWind_speed_knots(Integer wind_speed_knots) {
        this.wind_speed_knots = wind_speed_knots;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getTakeouts() {
        return takeouts;
    }

    public void setTakeouts(Integer takeouts) {
        this.takeouts = takeouts;
    }

    public Integer getReturns() {
        return returns;
    }

    public void setReturns(Integer returns) {
        this.returns = returns;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getT_sum() {
        return t_sum;
    }

    public void setT_sum(Integer t_sum) {
        this.t_sum = t_sum;
    }

    public Integer getR_sum() {
        return r_sum;
    }

    public void setR_sum(Integer r_sum) {
        this.r_sum = r_sum;
    }

    public Integer getStatus_takeouts() {
        return status_takeouts;
    }

    public void setStatus_takeouts(Integer status_takeouts) {
        this.status_takeouts = status_takeouts;
    }

    public Integer getStatus_returns() {
        return status_returns;
    }

    public void setStatus_returns(Integer status_returns) {
        this.status_returns = status_returns;
    }

    @Override
    public String toString() {
        return station_id + " " + date + " " + time + " " + capacity + " " + takeouts + " " + returns + " " + available
                + " " + t_sum + " " + r_sum + " " +
                status_takeouts + " " + status_returns + " " +
                temp_max + " " + temp_max_raw
                + " " + temp_min + " " + temp_min_raw + " " + rain + " " + rain_raw + " " + wind_speed_knots;
    }
}