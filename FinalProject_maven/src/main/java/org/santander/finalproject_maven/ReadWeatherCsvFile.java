import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.ParseDateTime;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class ReadWeatherCsvFile {

    static final String CSV_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\all_data\\weather\\usage-2017-all-clean.csv";

    private static final String UPDATE_TABLE_WITH_WEATHER = "UPDATE public.status_practice3\n" +
            "\tSET temp_max=?, temp_max_raw=?, temp_min=?, temp_min_raw=?, rain=?, rain_raw=?, wind_speed_knots=?\n" +
            "\tWHERE station_id = ?\n" +
            "\tAND date = ? \n" +
            "\tAND time = ?;";

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        insertWeatherValues(CSV_FILENAME, conn);
    }

    private static void insertWeatherValues(String csvFileName, Connection connection) {

        try(ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(csvFileName), CsvPreference.STANDARD_PREFERENCE)) {

            final String[] headers = beanReader.getHeader(true);
            final CellProcessor[] readingProcessors = getProcessors();

            WeatherBean weatherBean;
            while ((weatherBean = beanReader.read(WeatherBean.class, headers, readingProcessors)) != null) {
//                System.out.println(weatherBean);
                try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TABLE_WITH_WEATHER)) {

                    preparedStatement.setString(1, weatherBean.getTemp_max());
                    preparedStatement.setDouble(2, weatherBean.getTemp_max_raw());
                    preparedStatement.setString(3, weatherBean.getTemp_min());
                    preparedStatement.setDouble(4, weatherBean.getTemp_min_raw());
                    preparedStatement.setString(5, weatherBean.getRain());
                    preparedStatement.setDouble(6, weatherBean.getRain_raw());
                    preparedStatement.setInt(7, weatherBean.getWind_speed_knots());

                    preparedStatement.setInt(8, weatherBean.getStation_id());
                    preparedStatement.setDate(9, new Date(weatherBean.getDate().getTime()));
                    preparedStatement.setTime(10, new Time(weatherBean.getDate().getTime()));

//                    System.out.println(preparedStatement);
                    preparedStatement.addBatch();
                    preparedStatement.executeBatch();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the processors used for the examples.
     */
    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new ParseInt()), // bike_id
                new NotNull(new ParseInt()), // station_id
                new NotNull(new ParseDate("dd/MM/yyyy HH:mm")), // date
                new NotNull(new ParseInt()), // month
                new NotNull(new ParseInt()), // day
                new NotNull(new ParseInt()), // hour
                new NotNull(), // temp_max
                new NotNull(new ParseDouble()), // temp_max_raw
                new NotNull(), // temp_min
                new NotNull(new ParseDouble()), // temp_min_raw
                new NotNull(), // rain
                new NotNull(new ParseDouble()), // rain_raw
                new NotNull(new ParseInt()) // wind_speed_knots
        };
        return processors;

    }

}
