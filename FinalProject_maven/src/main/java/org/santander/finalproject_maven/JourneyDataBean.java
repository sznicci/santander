/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.util.Date;

/**
 *
 * @author sznicci
 */
public class JourneyDataBean {

    /**
     * Fields
     */
    private Integer rentalId;
    private Integer duration;
    private Integer bikeId;
    private Date endDate;
    private Integer endStationId;
    private String endStationName;
    private Date startDate;
    private Integer startStationId;
    private String startStationName;

    public JourneyDataBean() {
    }

    public JourneyDataBean(Integer rentalId, Integer duration, Integer bikeId, Date endDate, Integer endStationId, String endStationName, Date startDate, Integer startStationId, String startStationName) {
        this.rentalId = rentalId;
        this.duration = duration;
        this.bikeId = bikeId;
        this.endDate = endDate;
        this.endStationId = endStationId;
        this.endStationName = endStationName;
        this.startDate = startDate;
        this.startStationId = startStationId;
        this.startStationName = startStationName;
    }

    public Integer getRentalId() {
        return rentalId;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Integer getEndStationId() {
        return endStationId;
    }

    public String getEndStationName() {
        return endStationName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Integer getStartStationId() {
        return startStationId;
    }

    public String getStartStationName() {
        return startStationName;
    }

    public void setRentalId(Integer rentalId) {
        this.rentalId = rentalId;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndStationId(Integer endStationId) {
        this.endStationId = endStationId;
    }

    public void setEndStationName(String endStationName) {
        this.endStationName = endStationName;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartStationId(Integer startStationId) {
        this.startStationId = startStationId;
    }

    public void setStartStationName(String startStationName) {
        this.startStationName = startStationName;
    }

    @Override
    public String toString() {
        return "JourneyDataBean{" + "rentalId=" + rentalId + ", duration=" + duration + ", bikeId=" + bikeId + ", endDate=" + endDate + ", endStationId=" + endStationId + ", endStationName=" + endStationName + ", startDate=" + startDate + ", startStationId=" + startStationId + ", startStationName=" + startStationName + '}';
    }

}
