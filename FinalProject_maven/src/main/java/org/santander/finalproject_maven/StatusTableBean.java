/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.santander.finalproject_maven;

import java.sql.Time;
import java.sql.Date;

/**
 *
 * @author Research
 */
public class StatusTableBean {
    
    private Integer id;
    private Integer stationId;
    private Date date;
    private Time time;
    private Integer capacity;
    private Integer takout;
    private Integer returns;
    private Integer available;
    private Integer status;
    
    public StatusTableBean() {
        
    }

    public StatusTableBean(Integer id, Integer stationId, Date date, Time time, Integer capacity, Integer takout, Integer returns, Integer available, Integer status) {
        this.id = id;
        this.stationId = stationId;
        this.date = date;
        this.time = time;
        this.capacity = capacity;
        this.takout = takout;
        this.returns = returns;
        this.available = available;
        this.status = status;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getTakout() {
        return takout;
    }

    public void setTakout(Integer takout) {
        this.takout = takout;
    }

    public Integer getReturns() {
        return returns;
    }

    public void setReturns(Integer returns) {
        this.returns = returns;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusTableBean{" + "id=" + id + ", stationId=" + stationId + ", date=" + date + ", time=" + time + ", capacity=" + capacity + ", takout=" + takout + ", returns=" + returns + ", available=" + available + ", status=" + status + '}';
    }
       
}
