import java.util.Date;

public class WeatherBean {

    private Integer bike_id;
    private Integer station_id;
    private Date date;
    private Integer month;
    private Integer day;
    private Integer hour;
    private String temp_max;
    private Double temp_max_raw;
    private String temp_min;
    private Double temp_min_raw;
    private String rain;
    private Double rain_raw;
    private Integer wind_speed_knots;



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

    @Override
    public String toString() {
        return bike_id + " " + station_id + " " + date + " " + month + " " + day + " " + hour + " " + temp_max + " " + temp_max_raw
                + " " + temp_min + " " + temp_min_raw + " " + rain + " " + rain_raw + " " + wind_speed_knots;
    }
}
