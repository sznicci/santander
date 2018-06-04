/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sznicci
 */
public class BikePointJsonFileRead {

    static final String JSON_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\Tasks\\000TaskFiles\\bikePointJSON\\BikePoint.json";

    public static BikePointBean parseThrough(JsonElement je) throws JsonParseException {
        JsonObject jsonObj = je.getAsJsonObject();

        String id = jsonObj.get("id").getAsString();
        String url = jsonObj.get("url").getAsString();
        String commonName = jsonObj.get("commonName").getAsString();
        String placeType = jsonObj.get("placeType").getAsString();

        String category = jsonObj.get("additionalProperties").getAsJsonObject().get("category").getAsString();
//        String category = jsonObj.get("category").getAsString();
//        String key = jsonObj.get("key").getAsString();
//        String sourceSystemKey = jsonObj.get("sourceSystemKey").getAsString();
//        int value = jsonObj.get("value").getAsInt();

        AdditionalProperties additionalProperties = new AdditionalProperties(category, "key", "sourceKey", 0);
        Double latitude = jsonObj.get("latitude").getAsDouble();
        Double longitude = jsonObj.get("longitude").getAsDouble();

        return new BikePointBean(id, url, commonName, placeType, additionalProperties, latitude, longitude);
    }

    
    public static void main(String[] args) {
        BikePointBean bikePoint;
        BufferedReader bfReader = null;

        try {
            bfReader = new BufferedReader(new FileReader(JSON_FILENAME));
            JsonReader jsonReader = new JsonReader(new FileReader(JSON_FILENAME));
            JsonObject jObj = null;
            Gson gson = new Gson();
            BikePointBean bikeP;


        } catch (FileNotFoundException ex) {
            Logger.getLogger(BikePointJsonFileRead.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BikePointJsonFileRead.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
