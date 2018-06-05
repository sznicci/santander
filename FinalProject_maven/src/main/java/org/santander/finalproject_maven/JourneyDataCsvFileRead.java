/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author sznicci
 */
public class JourneyDataCsvFileRead {

    static final String CSV_PATH = "K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataCSVFiles\\2017\\";

    /**
     * Generate full path for CSV file
     *
     * @return - Full path for selected CSV file
     */
    private static String getCsvFileFullPath() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, type in the folder name and file name of the preferred CSV file.\n"
                + "Example input for April to June: \'april-june\\\\52JourneyDataExtract05Apr2017-11Apr2017.csv\'");
        String fileName = scanner.nextLine();

        return CSV_PATH + fileName;
    }

    /**
     * Generate SQL statement for creating usage tables for selected quarters
     *
     * @param quarterNumber - represents the quarter in the year. E.g.: 1 -> Q1
     * @return - SQL statement for creating a table as a String.
     */
    private static String sqlStatementForCreateTables(int quarterNumber) {
        return "CREATE TABLE usageQ" + quarterNumber + " ( "
                + "id serial PRIMARY KEY, "
                + "bikeId int, "
                + "date date, "
                + "start_time time, "
                + "end_time time, "
                + "start_station_id int, "
                + "end_station_id int "
                + ");";
    }

    /**
     * Generate SQL insert statement for inserting data into selected quarter
     * table
     *
     * @param quarterNumber - represents the quarter in the year. E.g.: 1 -> Q1
     * @return - SQL statement for inserting into a table as a String.
     */
    private static String sqlInsertForJourneyData(int quarterNumber) {
        return "INSERT INTO public.usageQ" + quarterNumber + "(\n"
                + "bikeId, date, start_time, end_time, start_station_id, end_station_id)\n"
                + "VALUES (?, ?, ?, ?, ?, ?);";
    }

    /**
     * Checks whether a table exists in the database or not
     *
     * @param conn - database connection
     * @param quarterNumber - represents the quarter in the year. E.g.: 1 -> Q1
     * @return - true if table exists, false otherwise
     */
    private static boolean hasTable(Connection conn, int quarterNumber) {
        try {
            String tableName = "usageq" + quarterNumber;
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet rs = dbm.getTables(null, null, tableName, null);

            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(JourneyDataCsvFileRead.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    protected static void insertIntoUsageTable(String path, int quarterNumber) {

        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();
//        String file = getCsvFileFullPath();
        PreparedStatement ps;

        if (!hasTable(conn, quarterNumber)) {
            Statement statement = null;

            try {
                statement = conn.createStatement();
                statement.executeUpdate(sqlStatementForCreateTables(quarterNumber));
                statement.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try (ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(path), CsvPreference.STANDARD_PREFERENCE)) {

            // Skip the defined header if it exists
            beanReader.getHeader(true);
            
            // Define headers for csv file because they do not match with JourneyDataBean fields
            final String[] headers = new String[]{"rentalId", "duration", "bikeId",
                "endDate", "endStationId", "endStationName", "startDate", "startStationId", "startStationName"};
            final CellProcessor[] processors = getProcessors();

            JourneyDataBean journeyData;
            UsageBean usage = new UsageBean();
            long endTime, startTime;

//            int i = 0;
            while ((journeyData = beanReader.read(JourneyDataBean.class, headers, processors)) != null) {
                // Check whether there is an incomplete record
                if (!journeyData.isComplete()) {
                    continue;
                }
                
                // Set fields for usage
                usage.setDate(new java.sql.Date(journeyData.getStartDate().getTime()));
                endTime = journeyData.getEndDate().getTime();
                usage.setEndTime(new Time(endTime));
                usage.setEndStationId(journeyData.getEndStationId());
                startTime = journeyData.getStartDate().getTime();
                usage.setStartTime(new Time(startTime));
                usage.setStartStationId(journeyData.getStartStationId());
                usage.setBikeId(journeyData.getBikeId());

                // Create SQL statement
                ps = insertStatementForUsageTable(conn, sqlInsertForJourneyData(quarterNumber), usage);

                // Insert into database
                ps.addBatch();
                ps.executeBatch();

//                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Create SQL insert statement
     */
    private static PreparedStatement insertStatementForUsageTable(Connection conn, String SQL, UsageBean usage) {

        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(SQL);

            statement.setInt(1, usage.getBikeId());
            statement.setDate(2, usage.getDate());
            statement.setTime(3, usage.getStartTime());
            statement.setTime(4, usage.getEndTime());
            statement.setInt(5, usage.getStartStationId());
            statement.setInt(6, usage.getEndStationId());

        } catch (SQLException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }

        return statement;
    }

    /**
     * Sets up the processors.
     */
    private static CellProcessor[] getProcessors() {

        final CellProcessor[] processors = new CellProcessor[]{
            new Optional(new ParseInt()), // rentalId
            new Optional(new ParseInt()), // duration
            new Optional(new ParseInt()), // bikeId
            new Optional(new ParseDate("dd/MM/yyyy mm:ss")), // endDate
            new Optional(new ParseInt()), // endStationId
            new Optional(), // endStationName
            new Optional(new ParseDate("dd/MM/yyyy mm:ss")), // startDate
            new Optional(new ParseInt()), // startStationId
            new Optional(), // startStationName
        };
        return processors;
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, select a quarter (e.g.: 1, 2, 3 or 4): ");
        int quarter;
        if ((quarter = scanner.nextInt()) < 1 || quarter > 4) {
            throw new IllegalArgumentException("Selected number is not in range of 1 to 4. It must be 1, 2, 3 or 4.");
        }
        
        try {
            BufferedReader br = new BufferedReader(new FileReader("K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataCSVFiles\\2017\\jul-sept\\filenames.txt"));
            String line = null;
            String path = null;
            
            while ((line = br.readLine()) != null) {
                path = "jul-sept\\\\" + line;
                insertIntoUsageTable(CSV_PATH + path, quarter);
                System.out.println("Done " + path);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileNamesReader.class.getName()).log(Level.SEVERE, null, ex);
        }
//        insertIntoUsageTable(quarter);
    }

}
