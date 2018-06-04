/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;

/**
 *
 * @author sznicci
 */
public class UsageBean implements Serializable {
    
    /**
     * Attributes
     */
    private Integer usageId;
    private Integer bikeId;
    private Date date;
    private Time startTime;
    private Time endTime;
    private int startStationId;
    private int endStationId;

    public UsageBean() {
    }

    public UsageBean(Integer usageId, Date date, Time startTime, Time endTime, int startStationId, int endStationId) {
        this.usageId = usageId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startStationId = startStationId;
        this.endStationId = endStationId;
    }

    public Integer getUsageId() {
        return usageId;
    }

    public Integer getBikeId() {
        return bikeId;
    }
    
    public Date getDate() {
        return date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public int getStartStationId() {
        return startStationId;
    }

    public int getEndStationId() {
        return endStationId;
    }

    public void setUsageId(Integer usageId) {
        this.usageId = usageId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public void setStartStationId(int startStationId) {
        this.startStationId = startStationId;
    }

    public void setEndStationId(int endStationId) {
        this.endStationId = endStationId;
    }

    @Override
    public String toString() {
        return "UsageBean{" + "usageId=" + usageId + ", date=" + date + ", startTime=" + startTime + ", endTime=" + endTime + ", startStationId=" + startStationId + ", endStationId=" + endStationId + '}';
    }
    
}
