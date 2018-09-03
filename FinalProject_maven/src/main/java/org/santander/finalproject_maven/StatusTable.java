/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Triplet;
import org.javatuples.Tuple;

/**
 * @author sznicci
 */
public class StatusTable {

    private static final String FILE_PATH = "G:\\all_StationNames.txt";

    private static final String SQL_CREATE_STATUS_TABLE = "CREATE TABLE status (\n"
            + "station_id integer NOT NULL,\n"
            + "date date NOT NULL,\n"
            + "\"time\" time without time zone NOT NULL,\n"
            + "capacity integer,\n"
            + "takeouts integer,\n"
            + "returns integer,\n"
            + "available integer,\n"
            + "status integer,\n"
            + "CONSTRAINT status_pkey PRIMARY KEY (station_id, date, \"time\")"
            + ");";

    private static final String SQL_GET_STATION_ID = "SELECT station_id, capacity\n"
            + "FROM public.dock_station_info\n"
            + "WHERE common_name = ?;";

    private static final String SQL_INSERT_GENERATED_VALUES = "INSERT INTO public.status(\n"
            + "	station_id, date, \"time\", capacity)\n"
            + "	VALUES (?, ?, ?, ?);";

    private static final String SQL_SELECT_TAKEOUTS = "SELECT COUNT(id)\n"
            + "	FROM public.usage_selected_stations\n"
            + "	WHERE start_station_id = ? AND "
            + " date = ? AND "
            + " start_time = ?;";

    private static final String SQL_SELECT_RETURNS = "SELECT COUNT(id)\n"
            + "	FROM public.usage_selected_stations\n"
            + "	WHERE end_station_id = ? AND "
            + " date = ? AND "
            + " end_time = ?;";

    private static final String SQL_INSERT_TAKEOUT_VALUES = "INSERT INTO public.status (" +
            " station_id, date, \"time\", takeouts)\n" +
            "\tSELECT start_station_id, date, start_time, count(start_time) as takeouts\n" +
            "\tFROM public.usage_selected_stations\n" +
            "\tWHERE start_station_id = ?\n" +
            "\tGROUP BY start_station_id, start_time, date\n" +
            "\tORDER BY date, start_time;";

    private static final String SQL_INSERT_CAPACITY = "UPDATE public.status \n" +
            "\tSET capacity = ?\n" +
            "\tWHERE station_id = ?;";

    private static final String SQL_UPSERT_RETURNS = "INSERT INTO public.status (station_id, date, time, returns)\n" +
            "(SELECT end_station_id, date, end_time, count(end_time) as returns\n" +
            "FROM public.usage_selected_stations\n" +
            "WHERE end_station_id = ?\n" +
            "GROUP BY end_station_id, end_time, date\n" +
            "ORDER BY date, end_time)\n" +
            "ON CONFLICT (station_id, date, time)\n" +
            "DO UPDATE \n" +
            "SET returns = excluded.returns;";

    /**
     * Get selected station id from dock station info table
     *
     * @param conn
     * @return - station id if the selected station exists otherwise -1
     */
    protected static TreeMap<Integer, Integer> getStationIdAndCapacity(Connection conn) {
        TreeMap<Integer, Integer> stationIdAndCapacity = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
             PreparedStatement ps = conn.prepareStatement(SQL_GET_STATION_ID)) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!(line.equals("tube") || line.equals("rails") || line.equals("less20") || line.equals("other"))) {
                    ps.setString(1, line);

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    stationIdAndCapacity.put(rs.getInt("station_id"), rs.getInt("capacity"));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stationIdAndCapacity;
    }

    protected static List<Time> getTime() {
        List<Time> timeList = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            for (int i = 0; i < 60; i++) {
                timeList.add(new Time(h, i, 0));
            }
        }

        return timeList;
    }

    /**
     * Title: Get All Dates Between Two Dates source code Author: baeldung Date:
     * 2018 Code version: 0 Availability:
     * http://www.baeldung.com/java-between-dates
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<LocalDate> getDatesBetweenUsingJava8(
            LocalDate startDate, LocalDate endDate) {

        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());
    }

    /**
     * Insert first 4 columns into Status table
     *
     * @param conn
     * @param idAndCapacity - selected station ids and their capacities in a
     *                      TreeMap<Integer, Integer>
     * @param days          - days within a period in a List<Date>
     * @param timeList      - times within a day in a List<Time>
     */
    protected static void insertGeneratedValues(Connection conn, TreeMap<Integer, Integer> idAndCapacity, List<LocalDate> days, List<Time> timeList) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_GENERATED_VALUES)) {

            idAndCapacity.entrySet().forEach((e) -> {
                try {
                    ps.setInt(1, e.getKey());
                    ps.setInt(4, e.getValue());

                    for (int i = 0; i < days.size(); i++) {
                        ps.setDate(2, Date.valueOf(days.get(i)));
                        for (int j = 0; j < timeList.size(); j++) {
                            ps.setTime(3, timeList.get(j));

                            ps.execute();
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static void insertCalculatedValues(Connection conn, int stationId, int capacity) {
        try (
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT_TAKEOUT_VALUES);
                PreparedStatement ps2 = conn.prepareStatement(SQL_INSERT_CAPACITY);
                PreparedStatement ps3 = conn.prepareStatement(SQL_UPSERT_RETURNS)) {

            ps.setInt(1, stationId);
            ps.execute();

            ps3.setInt(1, stationId);
            ps3.execute();

            ps2.setInt(1, capacity);
            ps2.setInt(2, stationId);
            ps2.execute();

        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static int takeouts(Connection conn, Integer startStationId, Date date, Time startTime) {
        try (PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT_TAKEOUTS)) {

            preparedStatement.setInt(1, startStationId);
            preparedStatement.setDate(2, date);
            preparedStatement.setTime(3, startTime);
//            System.out.println("takeout " + ps);

            ResultSet rs = preparedStatement.executeQuery();
            rs.next();

            return rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private static int returns(Connection conn, Integer endStationId, Date date, Time endTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_RETURNS)) {

            ps.setInt(1, endStationId);
            ps.setDate(2, date);
            ps.setTime(3, endTime);
//            System.out.println("return " + ps);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private static Triplet<Integer, Integer, Integer> available(Connection conn, Integer stationId, Integer currentCapacity, Date date, Time time) {
        int t = takeouts(conn, stationId, date, time);
        int r = returns(conn, stationId, date, time);
        Triplet<Integer, Integer, Integer> idAndCapacityAndAvailable = Triplet.with(stationId, currentCapacity, currentCapacity - t + r);

        System.out.println("takeout " + t);
        System.out.println("return " + r);
        System.out.println("triplet " + idAndCapacityAndAvailable);
        return idAndCapacityAndAvailable;
    }

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        // Check whether there is a table in the database for the selected quarter or not
        if (!DBConnection.hasTable(conn, "status")) {

            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(SQL_CREATE_STATUS_TABLE);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        TreeMap<Integer, Integer> station = getStationIdAndCapacity(conn);

        for (Map.Entry<Integer, Integer> entry : station.entrySet()) {
            System.out.println("station " + entry.getKey() + " capacity " + entry.getValue() + "\n");
            insertCalculatedValues(conn, entry.getKey(), entry.getValue());
        }

    }

}