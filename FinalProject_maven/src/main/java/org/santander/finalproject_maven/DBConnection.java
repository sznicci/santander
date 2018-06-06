/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sznicci
 */
public class DBConnection {

    /**
     * Set the connection string, user, and password
     */
    private final String url = "jdbc:postgresql://localhost/santander";
    private final String user = "postgres";
    private final String password = "Gr4vey4rd";

    /**
     * Connect to postgreSQL server
     *
     * @return Connection
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
//            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
    
    /**
     * Checks whether a table exists in the database or not
     *
     * @param conn - database connection
     * @param tableName - table name to check
     * @return - true if table exists, false otherwise
     */
    protected static boolean hasTable(Connection conn, String tableName) {
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            try (ResultSet rs = dbm.getTables(null, null, tableName, null)) {
                if (rs.next()) {
                    return true;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(JourneyDataCsvFileRead.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

}
