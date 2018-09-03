import java.sql.*;
import java.util.ArrayList;

public class UpdateStatusCalculation {

    private static final int TAKEOUT_RED = 1;
    private static final int TAKEOUT_AMBER = 2;
    private static final int TAKEOUT_BLUE = 3;
    private static final int TAKEOUT_GREEN = 4;
    private static final int RETURN_RED = 1;
    private static final int RETURN_AMBER = 2;
    private static final int RETURN_BLUE = 3;
    private static final int RETURN_GREEN = 4;

    private static final double FIRST_BOUNDARY = 0;
    private static final double SECOND_BOUNDARY = 0.25;
    private static final double THIRD_BOUNDARY = 0.5;
    private static final double FOURTH_BOUNDARY = 0.75;

    private static final String UPDATE_TABLE_WITH_STATUS_TAKEOUT = "UPDATE public.status\n" +
            "\tSET status_takeouts = ?, status_returns = ?\n" +
            "\tWHERE station_id = ?\n" +
            "\tAND date = ? \n" +
            "\tAND time = ?;";

    private static final String SELECT_AVAILABLES = "SELECT station_id, date, \"time\", capacity, available\n" +
            "\tFROM public.status;";

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        insertStatusTakeoutValues(conn);
    }

    private static void insertStatusTakeoutValues(Connection connection) {

        ArrayList<StatusCalculationBean> availableWithTakeout = calculateStatusCategoryTakeout(getAvailable(connection));
        ArrayList<StatusCalculationBean> availableWithReturn = calculateStatusCategoryReturn(getAvailable(connection));

        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TABLE_WITH_STATUS_TAKEOUT)) {

            for (int i = 0; i < availableWithTakeout.size(); i++) {
                preparedStatement.setInt(1, availableWithTakeout.get(i).getStatus_takeouts());
                preparedStatement.setInt(2, availableWithReturn.get(i).getStatus_returns());

                preparedStatement.setInt(3, availableWithTakeout.get(i).getStation_id());
                preparedStatement.setDate(4, new Date(availableWithTakeout.get(i).getDate().getTime()));
                preparedStatement.setTime(5, availableWithTakeout.get(i).getTime());

//                System.out.println("avail " + availableWithTakeout.get(i));
//                System.out.println(avail.getStatus_takeouts());

                preparedStatement.addBatch();
            }
            System.out.println("takeout");
            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static ArrayList<StatusCalculationBean> calculateStatusCategoryTakeout(ArrayList<StatusCalculationBean> available) {

        for (StatusCalculationBean avail : available) {
            if ( (avail.getAvailable() <= avail.getCapacity() * SECOND_BOUNDARY) && (avail.getAvailable() >= FIRST_BOUNDARY) ) {
                avail.setStatus_takeouts(TAKEOUT_RED);
            } else if ( (avail.getAvailable() <= avail.getCapacity() * THIRD_BOUNDARY) && (avail.getAvailable() > SECOND_BOUNDARY) ) {
                avail.setStatus_takeouts(TAKEOUT_AMBER);
            } else if ( (avail.getAvailable() <= avail.getCapacity() * FOURTH_BOUNDARY) && (avail.getAvailable() > THIRD_BOUNDARY) ) {
                avail.setStatus_takeouts(TAKEOUT_BLUE);
            } else if ( (avail.getAvailable() <= avail.getCapacity()) && (avail.getAvailable() > FOURTH_BOUNDARY) ) {
                avail.setStatus_takeouts(TAKEOUT_GREEN);
            }
//            System.out.println("t " + avail);
        }

        return available;
    }

    private static ArrayList<StatusCalculationBean> calculateStatusCategoryReturn(ArrayList<StatusCalculationBean> available) {

        for (StatusCalculationBean avail : available) {
            if ( (avail.getAvailable() <= avail.getCapacity() * SECOND_BOUNDARY) && (avail.getAvailable() >= FIRST_BOUNDARY) ) {
                avail.setStatus_returns(RETURN_GREEN);
            } else if ( (avail.getAvailable() <= avail.getCapacity() * THIRD_BOUNDARY) && (avail.getAvailable() > SECOND_BOUNDARY) ) {
                avail.setStatus_returns(RETURN_BLUE);
            } else if ( (avail.getAvailable() <= avail.getCapacity() * FOURTH_BOUNDARY) && (avail.getAvailable() > THIRD_BOUNDARY) ) {
                avail.setStatus_returns(RETURN_AMBER);
            } else if ( (avail.getAvailable() <= avail.getCapacity()) && (avail.getAvailable() > FOURTH_BOUNDARY) ) {
                avail.setStatus_returns(RETURN_RED);
            }
//            System.out.println("r " + avail);
        }

        return available;
    }

    private static ArrayList<StatusCalculationBean> getAvailable(Connection connection) {
        ArrayList<StatusCalculationBean> available = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLES)) {

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                StatusCalculationBean statusCalculationBean = new StatusCalculationBean();
                statusCalculationBean.setStation_id(rs.getInt(1));
                statusCalculationBean.setDate(rs.getDate(2));
                statusCalculationBean.setTime(rs.getTime(3));
                statusCalculationBean.setCapacity(rs.getInt(4));
                statusCalculationBean.setAvailable(rs.getInt(5));

                available.add(statusCalculationBean);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return available;
    }

}
