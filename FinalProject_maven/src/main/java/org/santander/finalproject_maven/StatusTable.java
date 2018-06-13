/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
            + "return int,\n"
            + "available int,\n"
            + "status int\n"
            + ");";
    
    protected static void createStatusTable(Connection con) {
        try (PreparedStatement ps = con.prepareStatement(SQL_CREATE_STATUS_TABLE)) {
            ps.execute();
        } catch (SQLException ex) {
            Logger.getLogger(StatusTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getSQL_CREATE_STATUS_TABLE() {
        return SQL_CREATE_STATUS_TABLE;
    }
    
}
