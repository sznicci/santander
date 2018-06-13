/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

/**
 *
 * @author sznicci
 */
public class BikePointBean {
    
    /**
     * Fields
     */
    private String id;
    private Integer stationId;
    private String url;
    private String commonName;
    private String placeType;
    private AdditionalProperties additionalProperties;
    private Double latitude;
    private Double longitude;

    public BikePointBean() {
    }

    public BikePointBean(String id, Integer stationId, String url, String commonName, String placeType, AdditionalProperties additionalProperties, Double latitude, Double longitude) {
        this.id = id;
        this.stationId = stationId;
        this.url = url;
        this.commonName = commonName;
        this.placeType = placeType;
        this.additionalProperties = additionalProperties;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public Integer getStationId() {
        return stationId;
    }

    public String getUrl() {
        return url;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getPlaceType() {
        return placeType;
    }

    public AdditionalProperties getAdditionalProperties() {
        return additionalProperties;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public void setAdditionalProperties(AdditionalProperties additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
}
