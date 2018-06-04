/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author sznicci
 */
public class JsonBikePointRead {
    
    static final String JSON_FILENAME = "G:\\ARU\\Modules\\Semester3_FinalProject\\Tasks\\000TaskFiles\\bikePointJSON\\BikePoint.json";
    
    protected static ArrayList<LinkedTreeMap> readFile(String file) {
        JsonReader jsonReader = null;
        ArrayList<LinkedTreeMap> jsonArrayList = null;
        Gson gson = new Gson();
        
        try {
            jsonReader = new JsonReader(new FileReader(file));
            
            jsonArrayList = gson.fromJson(jsonReader, ArrayList.class);
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return jsonArrayList;
    }
    
    protected static void getJsonElement(ArrayList jsonArrayList) {
        BikePointBean bikePoint = new BikePointBean();
        
        // Json properties
        String id;
        String url;
        String commonName;
        String placeType;
        
        // Properties for Additional Properties
        String category;
        String key;
        String sourceSystemKey;
        int value;
        
        AdditionalProperties additionalProperties;
        Double latitude;
        Double longitude;
        
        Gson gson = new Gson();
        JsonObject jo = null;
        LinkedTreeMap jsonLTM = null;
        
        System.out.println("asd " + jsonArrayList.get(0));
//        String firstEl = "{$type=Tfl.Api.Presentation.Entities.Place, Tfl.Api.Presentation.Entities, id=BikePoints_1, url=/Place/BikePoints_1, commonName=River Street , Clerkenwell, placeType=BikePoint, additionalProperties=[{$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=TerminalName, sourceSystemKey=BikePoints, value=001023, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=Installed, sourceSystemKey=BikePoints, value=true, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=Locked, sourceSystemKey=BikePoints, value=false, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=InstallDate, sourceSystemKey=BikePoints, value=1278947280000, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=RemovalDate, sourceSystemKey=BikePoints, value=, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=Temporary, sourceSystemKey=BikePoints, value=false, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=NbBikes, sourceSystemKey=BikePoints, value=18, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=NbEmptyDocks, sourceSystemKey=BikePoints, value=1, modified=2018-02-25T21:24:23.477Z}, {$type=Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities, category=Description, key=NbDocks, sourceSystemKey=BikePoints, value=19, modified=2018-02-25T21:24:23.477Z}], children=[], childrenUrls=[], lat=51.529163, lon=-0.10997}\n";
//        String firstEl = "{\"type\":\"Tfl.Api.Presentation.Entities.Place, Tfl.Api.Presentation.Entities\",\"id\":\"BikePoints_1\",\"url\":\"/Place/BikePoints_1\",\"commonName\":\"River Street , Clerkenwell\",\"placeType\":\"BikePoint\",\"additionalProperties\":[{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"TerminalName\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"001023\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"Installed\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"true\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"Locked\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"false\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"InstallDate\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"1278947280000\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"RemovalDate\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"Temporary\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"false\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"NbBikes\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"18\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"NbEmptyDocks\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"1\",\"modified\":\"2018-02-25T21:24:23.477Z\"},{\"type\":\"Tfl.Api.Presentation.Entities.AdditionalProperties, Tfl.Api.Presentation.Entities\",\"category\":\"Description\",\"key\":\"NbDocks\",\"sourceSystemKey\":\"BikePoints\",\"value\":\"19\",\"modified\":\"2018-02-25T21:24:23.477Z\"}],\"children\":[],\"childrenUrls\":[],\"lat\":51.529163,\"lon\":-0.10997}";
//        firstEl = firstEl.replace("$", "");
        
        
//        System.out.println("type: " + firstEl);
//        jo = gson.fromJson(firstEl, JsonObject.class);
        
//        System.out.println("type: " + jo.get("id"));
        
//        for (int i = 0; i < 50; i++) {
//            jsonLTM = gson.fromJson(jsonArrayList.get(i).toString(), LinkedTreeMap.class);
//            
//            id = jsonLTM.get("id").toString();
//            url = jsonLTM.get("url").toString();
////            commonName = jsonArrayList.get(i).get("commonName").toString();
////            placeType = jsonArrayList.get(i).get("placeType").toString();
//            
//            System.out.println("id: " + id + " url " + url);
//        }
        
//        return bikePoint;
    }
    
    public static void main(String[] args) {
//        System.out.println("asd: " + readFile(JSON_FILENAME).get(0));
        ArrayList<LinkedTreeMap> strA = readFile(JSON_FILENAME);
        getJsonElement(strA);
    }
}
