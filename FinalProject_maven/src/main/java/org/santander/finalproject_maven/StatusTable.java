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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sznicci
 */
public class StatusTable {

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

    private static final String SQL_GET_STATION_ID = "SELECT station_id\n"
            + "FROM public.dock_station_info\n"
            + "WHERE common_name = ?;";
    
    protected static int getStationId(Connection conn) {
        try (BufferedReader br = new BufferedReader(new FileReader(".txt"))) {
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 4;
    }

    public static void main(String[] args) {
        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        // Check wheter there is a table in the database for the selected quarter or not
        if (!DBConnection.hasTable(conn, "status")) {

            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(SQL_CREATE_STATUS_TABLE);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
