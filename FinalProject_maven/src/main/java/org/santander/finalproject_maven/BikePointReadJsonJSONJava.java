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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import static org.santander.finalproject_maven.UsageSelectedStations.SQL_CREATE;

/**
 *
 * @author sznicci
 */
public class BikePointReadJsonJSONJava {

    protected static final String JSON_FILENAME = "K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\dataFiles\\jsonBikePoints\\BikePoint.json";
    protected static final String SQL_INSERT_FOR_BIKE_POINTS = "INSERT INTO public.dock_station_info(\n"
            + "   station_id, common_name, capacity, latitude, longitude)\n"
            + "VALUES(?, ?, ?, ?, ?);";
    protected static final String SQL_CREATE_FOR_BIKE_POINTS = "CREATE TABLE dock_station_info(\n"
            + "id serial PRIMARY KEY, "
            + "station_id integer, "
            + "common_name varchar(100), "
            + "capacity integer, "
            + "latitude varchar(30), "
            + "longitude varchar(30) );";

    protected static final String FILE_STATION_NAMES = "K:\\NikolettaSzedljak\\finalProject\\preparingData\\santander\\docking-stations\\stationsToUse\\all_StationNames.txt";
    protected static final String SQL_ALTER_TABLE_ADD_CATEGORY = "ALTER TABLE public.dock_station_info\n"
            + "    DROP COLUMN IF EXISTS category;\n"
            + "\n"
            + "ALTER TABLE public.dock_station_info\n"
            + "    ADD COLUMN category integer;";

    protected static final String SQL_UPDATE_STATION_ROW = "UPDATE public.dock_station_info\n"
            + "	SET category=?\n"
            + "	WHERE common_name = ?;";

    public static void insertBikePointsIntoDockStationInfoTable(String fileName, Connection conn) throws IOException {

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_FOR_BIKE_POINTS)) {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(fileName));

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject bikePoints = (JSONObject) jsonArray.get(i);

                // Json properties
                String stationId = ((String) bikePoints.get("id")).replaceAll("BikePoints_", "");
                String commonName = (String) bikePoints.get("commonName");
                String latitude = bikePoints.get("lat").toString();
                String longitude = bikePoints.get("lon").toString();

                // Additional Properties
                JSONArray addProp = (JSONArray) bikePoints.get("additionalProperties");
                JSONObject element8 = (JSONObject) addProp.get(8);
                Integer value = Integer.parseInt(element8.get("value").toString());

                ps.setInt(1, Integer.parseInt(stationId));
                ps.setString(2, commonName);
                ps.setInt(3, value);
                ps.setString(4, latitude);
                ps.setString(5, longitude);

                ps.addBatch();
                ps.executeBatch();
            }

        } catch (FileNotFoundException | ParseException | SQLException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Add category column and set category if selected
     * 
     * @param conn - connection for database
     **/
    protected static void addCategoryToBikePoints(Connection conn) {
        try (Statement statement = conn.createStatement();
                BufferedReader br = new BufferedReader(new FileReader(FILE_STATION_NAMES));
                PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STATION_ROW)) {
            // add new column if it does not exist, otherwise drop column and add an empty one
            statement.executeUpdate(SQL_ALTER_TABLE_ADD_CATEGORY);

            // update station info records with category
            String line;
            int category = 0;
            while ((line = br.readLine()) != null) {
                // line contains category
                if (line.equals("tube") || line.equals("rails") || line.equals("less20") || line.equals("other")) {
                    switch (line) {
                        case "tube":
                            category = 1;
                            break;
                        case "rails":
                            category = 2;
                            break;
                        case "less20":
                            category = 3;
                            break;
                        case "other":
                            category = 4;
                            break;
                        default:
                            break;
                    }
                } else { // line does not contain a category
                    ps.setInt(1, category);
                    ps.setString(2, line);
                    
                    ps.execute();
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {

        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        if (!DBConnection.hasTable(conn, "dock_station_info")) {
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate(SQL_CREATE_FOR_BIKE_POINTS);
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        insertBikePointsIntoDockStationInfoTable(JSON_FILENAME, conn);
//            addCategoryToBikePoints(conn);

    }

}
