import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.*;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;

public class PrepareCSVFileForWeatherData {

    static final String START_CSV_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\all_data\\2017\\selectedStations\\startStationInfo.csv";
    static final String END_CSV_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\all_data\\2017\\selectedStations\\endStationInfo.csv";

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        createFile(START_CSV_FILENAME, "ready.csv", conn);
        createFile(END_CSV_FILENAME, "ready.csv", conn);
    }

    private static void createFile(String csvFileName, String readyFileName, Connection conn) {
        try(ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(csvFileName), CsvPreference.STANDARD_PREFERENCE);
            ICsvBeanWriter beanWriter = new CsvBeanWriter(new FileWriter(readyFileName, true), CsvPreference.STANDARD_PREFERENCE))
        {
            // the header elements are used to map the values to the bean
            final String[] headers = beanReader.getHeader(true);
            final CellProcessor[] readingProcessors = getProcessors();
            final CellProcessor[] writingProcessors = getWritingProcessors();

            StationForWeatherBean stationForWeatherBean;
            beanWriter.writeHeader(headers);
            int i = 0;
            while ((stationForWeatherBean = beanReader.read(StationForWeatherBean.class, headers, readingProcessors)) != null) {
                if (isSelectedStation(stationForWeatherBean.getStation_id(), conn)) {
                    beanWriter.write(stationForWeatherBean, headers, writingProcessors);
                    System.out.println(i);
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CellProcessor[] getWritingProcessors() {
        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new ParseInt()), // bike_id
                new NotNull(new ParseInt()), // station_id
                new NotNull(new FmtDate("dd/MM/yyyy")), // date
                new NotNull(new FmtLocalTime()), // time
                new NotNull(new ParseInt()), // month
                new NotNull(new ParseInt()), // day
                new NotNull(new ParseInt()) // hour
        };
        return processors;
    }

    private static boolean isSelectedStation(int stationId, Connection conn) {

        TreeMap<Integer, Integer> station = StatusTable.getStationIdAndCapacity(conn);

        for (Map.Entry<Integer, Integer> entry : station.entrySet()) {
            if (stationId == entry.getKey()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sets up the processors used for the examples.
     */
    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new ParseInt()), // bike_id
                new NotNull(new ParseInt()), // station_id
                new NotNull(new ParseDate("yyyy-MM-dd")), // date
                new NotNull(new ParseLocalTime()), // time
                new NotNull(new ParseInt()), // month
                new NotNull(new ParseInt()), // day
                new NotNull(new ParseInt()) // hour
        };
        return processors;

    }

}
