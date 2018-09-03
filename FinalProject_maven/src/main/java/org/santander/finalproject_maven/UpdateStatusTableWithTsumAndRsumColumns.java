import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class UpdateStatusTableWithTsumAndRsumColumns {

    private static final Double UPPER_BOUND = 0.75;

    private static final String ALTER_STATUS_TABLE = "ALTER TABLE public.status\n" +
            "    ADD COLUMN IF NOT EXISTS t_sum integer,\n" +
            "\tADD COLUMN IF NOT EXISTS r_sum integer;";

    private static final String UPDATE_STATUS_TABLE = "UPDATE public.status\n" +
            "\tSET t_sum=calc.t_sum, r_sum=calc.r_sum\n" +
            "\t\n" +
            "\tFROM (\n" +
            "\t\tSELECT station_id, date, time, capacity, takeouts, returns, \n" +
            "\tSUM(takeouts) over (ORDER BY station_id, date, time) as t_sum,\n" +
            "\tSUM(returns) over (ORDER BY station_id, date, time) as r_sum\n" +
            "\n" +
            "\tFROM public.status\n" +
            "\tWHERE\tpublic.status.station_id = ?\n" +
            "\t\tAND public.status.date = ?" +
            "\tGROUP BY station_id, date, time, capacity, takeouts, returns\n" +
            "\t) as calc\n" +
            "\t\n" +
            "\tWHERE public.status.station_id = calc.station_id AND public.status.date = calc.date \n" +
            "\tAND public.status.time = calc.time\n" +
            "\t;";

    private static final String SELECT_LIMITH_TSUM_RSUM = "SELECT t_sum, r_sum FROM \n" +
            "\tpublic.status\n" +
            "\t\twhere station_id = ?\n" +
            "\t\tAND date = ?" +
            "\t\tORDER BY date DESC, time DESC\n" +
            "\t\tLIMIT ?;";

    private static final String SELECT_TAKEOUTS_RETURNS = "SELECT takeouts, returns \n" +
            "FROM  public.status\n" +
            "\t\twhere station_id = ?\n" +
            "\t\tand date = ?\n" +
            "\t\tORDER BY date, time;";

    private static final String SELECT_TSUM_RSUM_WHEN_LESS_MOVEMENTS = "SELECT t_sum, r_sum \n" +
            "FROM  public.status\n" +
            "\t\twhere station_id = ?\n" +
            "\t\tand date = ?\n" +
            "\t\tORDER BY date desc, time desc;";

    public static final String UPDATE_AVAILABLE_COLUMN_PER_DAY = "UPDATE public.status\n" +
            "\tSET available = daily.ava\n" +
            "\tFROM (VALUES " +
            "(?, ?, to_date(?, 'yyyy-mm-dd'), CAST(? as time)) ) as daily (ava, station_id, date, time)\n" +
            "\tWHERE public.status.station_id = daily.station_id\n" +
            "\tAND public.status.date = daily.date\n" +
            "\tAND public.status.time = daily.time;";

    private static final String SELECT_TIMES = "SELECT \"time\"\n" +
            "\tFROM public.status" +
            "\tWHERE date = ?" +
            "\tAND station_id = ?;";

    /**
     * Update status table with takeouts sum and returns sum columns based on stations
     *
     * @param connection - connection to the database
     * @param stations   - list of the selected stations
     */
    private static void updateStatusTableWithTsumAndRsumColumns(Connection connection, TreeMap<Integer, Integer> stations, List<LocalDate> days) {
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS_TABLE)) {

            for (Map.Entry<Integer, Integer> entry : stations.entrySet()) {
                ps.setInt(1, entry.getKey());

                for (LocalDate day : days) {
                    ps.setDate(2, Date.valueOf(day));

                    ps.execute();
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void calculateAndUpdateAvailability(Connection connection, int stationId, int capacity, List<LocalDate> days) {
        ArrayList<Integer> initialState;
        List<int[]> takeoutsAndReturns;
        ArrayList<Integer> calculatedAvailabilityCheck;
        List<Time> times;
//        String query;

        for (LocalDate day : days) {
            System.out.println("station " + stationId + " day " + day);
            initialState = getInitialState(connection, stationId, capacity, day);
            takeoutsAndReturns = getTakeoutsAndReturns(connection, stationId, day);
            calculatedAvailabilityCheck = calculateAvailability(initialState, takeoutsAndReturns, capacity);
            times = getTimes(connection, stationId, day);

            // if availability is not 0 and the times and availability numbers are equal
            if (calculatedAvailabilityCheck != null && times.size() == calculatedAvailabilityCheck.size()) {

                try (PreparedStatement ps = connection.prepareStatement(UPDATE_AVAILABLE_COLUMN_PER_DAY)) {

                    for (int i = 0; i < times.size(); i++) {
                        ps.setInt(1, calculatedAvailabilityCheck.get(i));
                        ps.setInt(2, stationId);
                        ps.setDate(3, Date.valueOf(day));
                        ps.setTime(4, times.get(i));

                        ps.addBatch();
                    }

                    ps.executeBatch();

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    private static ArrayList<Integer> calculateAvailability(ArrayList<Integer> initialStates, List<int[]> tsumAndRsum,
                                                            int capacity) {
        // check whether tsumAndRsum is empty
        if (tsumAndRsum.isEmpty())
            return null;

        ArrayList<Integer> availability = new ArrayList<>();

        // calculate the first availability entry
        availability.add(checkAvailabilityOutOfBounds(capacity, initialStates.get(0), tsumAndRsum.get(0)[0], tsumAndRsum.get(0)[1]));

        // check whether tsumAndRsum has only one element
        if (tsumAndRsum.size() < 2) {
            return availability;
        }

        // calculate the rest depending on the first entry
        for (int i = 1; i < tsumAndRsum.size(); i++) {
            availability.add(checkAvailabilityOutOfBounds(capacity, availability.get(i - 1), tsumAndRsum.get(i)[0], tsumAndRsum.get(i)[1]));
        }

        return availability;
    }

    private static int checkAvailabilityOutOfBounds(int capacity, int availabilityPrevious, int takeouts, int returns) {
        int availability = (availabilityPrevious - takeouts + returns);

        if (availability > capacity) {
            return availabilityPrevious;
        } else if (availability < 0) {
            return availabilityPrevious;
        } else {
            return availability;
        }
    }

    private static List<int[]> getTakeoutsAndReturns(Connection connection, int stationId, LocalDate day) {
        List<int[]> tsumAndRsum = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_TAKEOUTS_RETURNS)) {

            ps.setInt(1, stationId);
            ps.setDate(2, Date.valueOf(day));

            ResultSet rs = ps.executeQuery();

            int i = 0;
            while (rs.next()) {
                tsumAndRsum.add(new int[2]);
                tsumAndRsum.get(i)[0] = rs.getInt(1);
                tsumAndRsum.get(i)[1] = rs.getInt(2);
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tsumAndRsum;
    }

    private static ArrayList<Integer> getInitialState(Connection connection, int stationId, int capacity, LocalDate day) {
        int limitForRow = (int) (capacity * UPPER_BOUND);
        ArrayList<Integer> initialStatesForDay = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_LIMITH_TSUM_RSUM);
             PreparedStatement ps2 = connection.prepareStatement(SELECT_TSUM_RSUM_WHEN_LESS_MOVEMENTS)) {

            ResultSet rs;

            ps.setInt(1, stationId);
            ps.setDate(2, Date.valueOf(day));
            ps.setInt(3, limitForRow - 1);

            rs = ps.executeQuery();

            // if result is not null
            if (rs.next()) {
                if (rs.getInt(1) == rs.getInt(2)) { // if last ones are equal -> insert the next bigger one
                    if (rs.next())
                        initialStatesForDay.add((rs.getInt(1) > rs.getInt(2)) ? capacity : 0);
                } else { // if last ones are not equal
                    initialStatesForDay.add((rs.getInt(1) > rs.getInt(2)) ? capacity : 0);
                }
            } else { // less movements than the limitForRow
                ps2.setInt(1, stationId);
                ps2.setDate(2, Date.valueOf(day));
                rs = ps2.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) == rs.getInt(2)) { // if last ones are equal -> insert the next bigger one
                        rs.next();
                        initialStatesForDay.add((rs.getInt(1) > rs.getInt(2)) ? capacity : 0);
                    } else { // if last ones are not equal
                        initialStatesForDay.add((rs.getInt(1) > rs.getInt(2)) ? capacity : 0);
                    }
                } else // no movement during a day
                    initialStatesForDay.add(capacity);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return initialStatesForDay;
    }

    private static List<Time> getTimes(Connection connection, int stationId, LocalDate day) {
        List<Time> times = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(SELECT_TIMES)) {

            ps.setDate(1, Date.valueOf(day));
            ps.setInt(2, stationId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                times.add(rs.getTime(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return times;
    }

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        try {
            PreparedStatement s = conn.prepareStatement(ALTER_STATUS_TABLE);
             s.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        TreeMap<Integer, Integer> stations = StatusTable.getStationIdAndCapacity(conn);
        List<LocalDate> allDays = StatusTable.getDatesBetweenUsingJava8(
                LocalDate.of(2017, Month.JANUARY, 1),
                LocalDate.of(2018, Month.JANUARY, 1));

        for (Map.Entry<Integer, Integer> station : stations.entrySet()) {
            calculateAndUpdateAvailability(conn, station.getKey(), station.getValue(), allDays);
        }

        updateStatusTableWithTsumAndRsumColumns(conn, stations, allDays);

    }

}
