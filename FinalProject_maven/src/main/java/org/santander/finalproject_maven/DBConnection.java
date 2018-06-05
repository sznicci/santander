/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

}
