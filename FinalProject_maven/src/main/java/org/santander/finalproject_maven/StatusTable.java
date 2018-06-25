/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

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
 *
 * @author sznicci
 */
public class StatusTable {

    private static final String FILE_PATH = "K:\\NikolettaSzedljak\\finalProject\\"
            + "preparingData\\santander\\docking-stations\\stationsToUse\\all_StationNames.txt";

    private static final String SQL_CREATE_STATUS_TABLE = "CREATE TABLE status (\n"
            + "id serial PRIMARY KEY,\n"
            + "station_id int,\n"
            + "date date,\n"
            + "time time,\n"
            + "capacity int,\n"
            + "takeout int,\n"
            + "returns int,\n"
            + "available int,\n"
            + "status int\n"
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

    private static final String SQL_SELECT_STATION_ID_AND_CAPACITY = "SELECT DISTINCT(start_station_id), capacity\n"
            + "	FROM public.usage_selected_stations, public.dock_station_info\n"
            + "	WHERE start_station_id = station_id and category IS NOT NULL\n"
            + "	order by start_station_id;";

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
     * TreeMap<Integer, Integer>
     * @param days - days within a period in a List<Date>
     * @param timeList - times within a day in a List<Time>
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
                    System.out.println("station ready" + System.nanoTime());
                } catch (SQLException ex) {
                    Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static void insertCalculatedValues(Connection conn) {

    }

    private static int takeouts(Connection conn, Integer startStationId, Date date, Time startTime) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_TAKEOUTS)) {

            ps.setInt(1, startStationId);
            ps.setDate(2, date);
            ps.setTime(3, startTime);
            System.out.println("takeout " + ps);

            ResultSet rs = ps.executeQuery();
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
            System.out.println("return " + ps);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getInt(1);

        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    private static Triplet<Integer, Integer, Integer> available(Connection conn, Integer stationId, Integer capacity, Date date, Time time) {
        int t = takeouts(conn, stationId, date, time);
        int r = returns(conn, stationId, date, time);
        Triplet<Integer, Integer, Integer> idAndCapacityAndAvalable = Triplet.with(stationId, capacity, capacity - t + r);

        System.out.println("takeout " + t);
        System.out.println("return " + r);
        System.out.println("triplet " + idAndCapacityAndAvalable);
        return idAndCapacityAndAvalable;
    }

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

//        // Check wheter there is a table in the database for the selected quarter or not
//        if (!DBConnection.hasTable(conn, "status")) {
//
//            try (Statement statement = conn.createStatement()) {
//                statement.executeUpdate(SQL_CREATE_STATUS_TABLE);
//            } catch (SQLException ex) {
//                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        System.out.println(takeouts(conn, 7, new Date(2016 - 1900, 11, 28), new Time(11, 4, 0)));
        System.out.println(returns(conn, 7, new Date(2016 - 1900, 11, 30), new Time(13, 16, 0)));
        available(conn, 7, 16, new Date(2016 - 1900, 11, 30), new Time(13, 16, 0));

//        // Generate dates 
//        List<LocalDate> days = getDatesBetweenUsingJava8(LocalDate.of(2016, Month.DECEMBER, 28),
//                LocalDate.of(2018, Month.JANUARY, 3));
//
//        // Generate times
//        List<Time> timeList = getTime();
//        
//        // Get station ids and capacities
//        TreeMap<Integer, Integer> idAndCapacity = getStationIdAndCapacity(conn);
//        
//        // Insert first 4 column into Status table
//        insertGeneratedValues(conn, idAndCapacity, days, timeList);
    }

}
