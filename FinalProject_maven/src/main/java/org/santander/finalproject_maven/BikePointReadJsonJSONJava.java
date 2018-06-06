/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author sznicci
 */
public class BikePointReadJsonJSONJava {

    static final String JSON_FILENAME = "K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataFiles\\jsonBikePoints\\BikePoint.json";
    static final String SQL_INSERT_FOR_BIKE_POINTS = "INSERT INTO public.dock_station_info(\n"
            + "   common_name, capacity, latitude, longitude)\n"
            + "VALUES(?, ?, ?, ?);";
    static final String SQL_CREATE_FOR_BIKE_POINTS = "CREATE TABLE dock_station_info(\n"
            + "id serial PRIMARY KEY, "
            + "common_name varchar(100), "
            + "capacity integer, "
            + "latitude varchar(30), "
            + "longitude varchar(30) );";

    public static void insertBikePointsIntoDockStationInfoTable(String fileName, Connection conn) throws IOException {

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_FOR_BIKE_POINTS)) {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(fileName));

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject bikePoints = (JSONObject) jsonArray.get(i);

                // Json properties
                String commonName = (String) bikePoints.get("commonName");
                String latitude = bikePoints.get("lat").toString();
                String longitude = bikePoints.get("lon").toString();

                // Additional Properties
                JSONArray addProp = (JSONArray) bikePoints.get("additionalProperties");
                JSONObject element8 = (JSONObject) addProp.get(8);
                Integer value = Integer.parseInt(element8.get("value").toString());

                ps.setString(1, commonName);
                ps.setInt(2, value);
                ps.setString(3, latitude);
                ps.setString(4, longitude);

                ps.addBatch();
                ps.executeBatch();
            }

        } catch (FileNotFoundException | ParseException | SQLException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws IOException {

        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();
        
        if(!DBConnection.hasTable(conn, "dock_station_info")) {
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(SQL_CREATE_FOR_BIKE_POINTS);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        insertBikePointsIntoDockStationInfoTable(JSON_FILENAME, conn);

    }

}
