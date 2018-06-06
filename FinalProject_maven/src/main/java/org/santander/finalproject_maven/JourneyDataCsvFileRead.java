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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
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

    static final String CSV_PATH = "K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataFiles\\2017\\";

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
        return "INSERT INTO public.usageq" + quarterNumber + "(\n"
                + "bikeId, date, start_time, end_time, start_station_id, end_station_id)\n"
                + "VALUES (?, ?, ?, ?, ?, ?);";
    }

    /**
     * Prepare an insert statement and will execute the query.
     *
     * @param conn - database connection
     * @param path - path for the CSV file
     * @param quarterNumber - represents the quarter in the year. E.g.: 1 -> Q1
     */
    protected static void insertIntoUsageTable(Connection conn, String path, int quarterNumber) {

        try (
                PreparedStatement ps = conn.prepareStatement(sqlInsertForJourneyData(quarterNumber));
                ICsvBeanReader beanReader = new CsvBeanReader(new FileReader(path), CsvPreference.STANDARD_PREFERENCE)) {

            // Skip the defined header if it exists
            beanReader.getHeader(true);

            // Define headers for csv file because they do not match with JourneyDataBean fields
            final String[] headers = new String[]{"rentalId", "duration", "bikeId",
                "endDate", "endStationId", "endStationName", "startDate", "startStationId", "startStationName"};
            final CellProcessor[] processors = getProcessors();

            JourneyDataBean journeyData;
            UsageBean usage = new UsageBean();
            long endTime, startTime;

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
                ps.setInt(1, usage.getBikeId());
                ps.setDate(2, usage.getDate());
                ps.setTime(3, usage.getStartTime());
                ps.setTime(4, usage.getEndTime());
                ps.setInt(5, usage.getStartStationId());
                ps.setInt(6, usage.getEndStationId());

                // Insert into database
                ps.addBatch();
                ps.executeBatch();

            }

        } catch (Exception e) {
            Logger.getLogger(FileNamesReader.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    /**
     * Sets up the processors for the usage tables.
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

        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        // Check wheter there is a table in the database for the selected quarter or not
        if (!DBConnection.hasTable(conn, "usageq" + quarter)) {

            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(sqlStatementForCreateTables(quarter));
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Read all the file names from a file and call the insert method with each CSV file
        try (
                BufferedReader br
                = new BufferedReader(new FileReader("K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataFiles\\2017\\oct-dec\\filenames.txt"))) {

            String line;
            String path;

            while ((line = br.readLine()) != null) {
                path = "oct-dec\\\\" + line;
                insertIntoUsageTable(conn, CSV_PATH + path, quarter);
                System.out.println("Done " + path + " time " + new Timestamp(System.currentTimeMillis()));
            }
        } catch (IOException ex) {
            Logger.getLogger(FileNamesReader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
