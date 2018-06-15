/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sznicci
 */
public class StationNumbers {

    static final String FILE_PATH = "K:\\NikolettaSzedljak\\finalProject\\"
            + "preparingData\\santander\\docking-stations\\";
    static final int QUARTER = 3;

    static final String SELECT_TO_GET_NUMBER_OF_STATIONS = "SELECT COUNT(id) "
            + "FROM public.usageq" + QUARTER + "\n"
            + "WHERE start_station_id = (SELECT station_id\n"
            + "	FROM public.dock_station_info where common_name = ?)\n"
            + "	OR end_station_id = (SELECT station_id\n"
            + "	FROM public.dock_station_info where common_name = ?);";

    protected static void getNumberOfStations(Connection conn, String stationName, String folderName, int quarter) {

        try (PreparedStatement ps = conn.prepareStatement(SELECT_TO_GET_NUMBER_OF_STATIONS)) {
            ps.setString(1, stationName);
            ps.setString(2, stationName);

            ResultSet rs = ps.executeQuery();
            rs.next();
            writeStationNameAndOccuranceToFile(folderName, stationName + " - "
                    + Integer.toString(rs.getInt(1)), quarter);
        } catch (Exception e) {
            Logger.getLogger(FileNamesReader.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void writeStationNameAndOccuranceToFile(String folderName, String data, int quarter) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH
                + folderName + "\\\\" + folderName + "_Occurance.txt", true))) {

            bw.write("Quarter " + quarter);
            bw.newLine();
            bw.write(data);
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected static void getStationNameFromFile(Connection conn, String path, String folderName, int quarter) {

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = br.readLine()) != null) {
                getNumberOfStations(conn, line, folderName, quarter);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StationNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, select a folder (e.g.: less20, other, rails or tube): ");
        String[] validInputs = {"less20", "other", "rails", "tube"};
        String folderAndFileName;
        if (!Arrays.asList(validInputs).contains(folderAndFileName = scanner.next())) {
            throw new IllegalArgumentException("Typed word is not one of the valid ones "
                    + "(e.g.: less20, other, rails or tube)");
        }

        // Set up database connection
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        String fullPath = FILE_PATH + "\\\\" + folderAndFileName + "\\\\" + folderAndFileName + "_StationNames.txt";

        getStationNameFromFile(conn, fullPath, folderAndFileName, QUARTER);
    }
}
