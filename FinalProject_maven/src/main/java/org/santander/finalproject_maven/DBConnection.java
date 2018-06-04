/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
     * Insert data into Usage table
     *
     * @param SQL statement to be inserted
     */
    protected void insertIntoUsage(String SQL) {
        Connection conn = connect();
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(SQL);

            statement.addBatch();
            statement.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                // Step 5 Close connection
                if (statement != null) {
                    statement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//
//    public static void main(String[] args) {
//        DBConnection app = new DBConnection();
////        app.insertIntoUsage(SQL);
//    }
}
