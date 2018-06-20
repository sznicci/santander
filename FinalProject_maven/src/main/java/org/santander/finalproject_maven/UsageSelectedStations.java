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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sznicci
 */
public class UsageSelectedStations {

    static final int QUARTER = 4;
    static final String FILE_PATH = "K:\\NikolettaSzedljak\\finalProject\\"
            + "preparingData\\santander\\docking-stations\\stationsToUse\\all_StationNames.txt";
    static final String SQL_CREATE = "CREATE TABLE usage_selected_stations ( "
            + "id serial PRIMARY KEY, "
            + "bikeId int, "
            + "date date, "
            + "start_time time, "
            + "end_time time, "
            + "start_station_id int, "
            + "end_station_id int "
            + ");";
    static final String SQL_INSERT = "INSERT INTO public.usage_selected_stations (\n"
            + "bikeId, date, start_time, end_time, start_station_id, end_station_id)\n"
            + "VALUES (?, ?, ?, ?, ?, ?);";

    static final String SQL_SELECT_FROM_OTHER_TABLE = "SELECT * FROM public.usageq" + QUARTER + "\n"
            + "WHERE start_station_id = (SELECT station_id\n"
            + "	FROM public.dock_station_info where common_name = ?)\n"
            + "	OR end_station_id = (SELECT station_id\n"
            + "	FROM public.dock_station_info where common_name = ?);";

    protected static void insertSelectedStations(String stationName, Connection conn) {
        try (PreparedStatement psSelect = conn.prepareStatement(SQL_SELECT_FROM_OTHER_TABLE);
                PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT)) {

            // prepare SQL select and save the result into a result set
            psSelect.setString(1, stationName);
            psSelect.setString(2, stationName);
            ResultSet rsSelect = psSelect.executeQuery();

            while (rsSelect.next()) {

                // prepare SQL insert
                psInsert.setInt(1, rsSelect.getInt("bikeId"));
                psInsert.setDate(2, rsSelect.getDate("date"));
                psInsert.setTime(3, rsSelect.getTime("start_time"));
                psInsert.setTime(4, rsSelect.getTime("end_time"));
                psInsert.setInt(5, rsSelect.getInt("start_station_id"));
                psInsert.setInt(6, rsSelect.getInt("end_station_id"));
                // Insert into database
                psInsert.addBatch();
                psInsert.executeBatch();
            }

        } catch (Exception e) {
            Logger.getLogger(FileNamesReader.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public static void main(String[] args) {

        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        // Check wheter there is a table in the database for the selected quarter or not
        if (!DBConnection.hasTable(conn, "usage_selected_stations")) {

            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(SQL_CREATE);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Get station names from file
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (! (line.equals("tube") || line.equals("rails") || line.equals("less20") || line.equals("other")) ) {
                    insertSelectedStations(line, conn);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
