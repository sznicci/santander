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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.geometric.PGpoint;

/**
 *
 * @author sznicci
 */
public class BikePointReadJsonJSONJava {

    static final String JSON_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\Tasks\\000TaskFiles\\bikePointJSON\\BikePoint.json";
    static final String SQL_INSERT_FOR_BIKE_POINTS = "INSERT INTO public.dock_station_info(\n" +
                                                    "   common_name, capacity, latitude_longitude)\n"
                                                    + "VALUES(?, ?, ?);";

    public static void createSQLQueryForInsert(String fileName) throws IOException {
        
        DBConnection dbConn = new DBConnection();
        Connection conn = dbConn.connect();

        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(fileName));

            for (int i = 0; i < 500; i++) {
                JSONObject bikePoints = (JSONObject) jsonArray.get(i);

                // Json properties
//                String id = (String) bikePoints.get("id");
                String commonName = (String) bikePoints.get("commonName");
//                commonName = commonName.replaceAll("\\s+(?=')", "\''");
//                commonName = commonName.replaceAll("\\s+(?=,)", "");
                Double latitude = Double.parseDouble(bikePoints.get("lat").toString());
                Double longitude = Double.parseDouble(bikePoints.get("lon").toString());

                // Additional Properties
                JSONArray addProp = (JSONArray) bikePoints.get("additionalProperties");
                JSONObject element8 = (JSONObject) addProp.get(8);
                Integer value = Integer.parseInt(element8.get("value").toString());

//                StringBuffer insertSqlStatement = new StringBuffer();
//                
//                insertSqlStatement.append(SQL_INSERT_FOR_BIKE_POINTS);
//                insertSqlStatement.append("default, ");
//                insertSqlStatement.append("\'").append(commonName).append("\', ");
//                insertSqlStatement.append(value).append(", ");
//                insertSqlStatement.append("POINT(").append(latitude).append(", ").append(longitude).append(")");
//                insertSqlStatement.append(");");

//                conn.insertIntoUsage(insertSqlStatement.toString());
//                System.out.println(insertSqlStatement);

                  insertBikePoints(conn, SQL_INSERT_FOR_BIKE_POINTS, commonName, value, latitude, longitude);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private static void insertBikePoints(Connection conn, String SQL, String commonName, Integer value, Double latitude, Double longitude) {
        PreparedStatement statement = null;
        
        PGpoint latLon = new PGpoint(latitude, longitude);
        
        try {
            statement = conn.prepareStatement(SQL);
            
//            statement.setObject(1, "default");
            statement.setString(1, commonName);
            statement.setInt(2, value);
            statement.setObject(3, latLon);
            
//            System.out.println("query: " + statement);
            
        } catch (SQLException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public static void main(String[] args) throws IOException {
        createSQLQueryForInsert(JSON_FILENAME);
    }

}
