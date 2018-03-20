/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

    static final String JSON_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\Tasks\\000TaskFiles\\bikePointJSON\\BikePoint.json";

    public static void parse(String fileName) throws IOException {

        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(fileName));

            for (Object o : jsonArray) {
                JSONObject bikePoints = (JSONObject) o;

                // Json properties
                String id = (String) bikePoints.get("id");
                String url = (String) bikePoints.get("url");
                String commonName;
                String placeType;
                Double latitude = Double.parseDouble(bikePoints.get("lat").toString());
                Double longitude = Double.parseDouble(bikePoints.get("lon").toString());

                // Properties for Additional Properties
                JSONArray addProp = (JSONArray) bikePoints.get("additionalProperties");
                JSONObject element8 = (JSONObject) addProp.get(8);
                String category = (String) element8.get("category");
                String key = (String) element8.get("key");
                String sourceSystemKey = (String) element8.get("sourceSystemKey");
                Integer value = Integer.parseInt(element8.get("value").toString());


                System.out.println("bike: " + id + " " + url + " " + value + " lat " + latitude + " lon " + longitude);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(BikePointReadJsonJSONJava.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) throws IOException {
        parse(JSON_FILENAME);
    }

}
