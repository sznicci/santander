/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Research
 */
public class StatusTableTest {
    
    private final DBConnection dbc = new DBConnection();
    private final Connection con = dbc.connect();

    @Test
    public void testCreateStatusTable() {
        try {
            con.setAutoCommit(false);
            StatusTable.createStatusTable(con);
            
            assertTrue(DBConnection.hasTable(con, "status"));
        } catch (SQLException ex) {
            Logger.getLogger(StatusTableTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.rollback();
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(StatusTableTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
