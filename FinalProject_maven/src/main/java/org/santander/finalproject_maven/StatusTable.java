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
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final String SQL_GET_STATION_ID = "SELECT station_id, category\n"
            + "FROM public.dock_station_info\n"
            + "WHERE common_name = ?;";

    /**
     * Get selected station id from dock station info table
     *
     * @param conn
     * @return - station id if the selected station exists otherwise -1
     */
    protected static TreeMap<Integer, Integer> getStationId(Connection conn) {
        TreeMap<Integer, Integer> stationIds = new TreeMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH));
                PreparedStatement ps = conn.prepareStatement(SQL_GET_STATION_ID)) {
            String line;
            int i = 0;

            while ((line = br.readLine()) != null) {
                if (!(line.equals("tube") || line.equals("rails") || line.equals("less20") || line.equals("other"))) {
                    ps.setString(1, line);

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    stationIds.put(rs.getInt("station_id"), rs.getInt("category"));
                    i++;
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stationIds;
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
        getStationId(conn).entrySet().forEach((e) -> {
            System.out.println("stat id " + e.getKey() + " value " + e.getValue());
        });
    }

}
