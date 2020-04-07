package com.budderfly.sites.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

public class SiteWorkOrdersDTO implements Serializable {

    private String workOrderNumber;

    private String scheduledDateAndTime;

    private String status;

    private String budderflyId;

    private String address;

    private String city;

    private String state;

    private String zip;

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public String getScheduledDateAndTime() {
        return scheduledDateAndTime;
    }

    public void setScheduledDateAndTime(String scheduledDateAndTime) {
        this.scheduledDateAndTime = scheduledDateAndTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBudderflyId() {
        return budderflyId;
    }

    public void setBudderflyId(String budderflyId) {
        this.budderflyId = budderflyId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String toString() {
        return "SiteWorkOrdersDTO{" +
            "workOrderNumber=" + getWorkOrderNumber() +
            ", scheduledDateAndTime='" + getScheduledDateAndTime() + "'" +
            ", status='" + getStatus() + "'" +
            ", budderflyId='" + getBudderflyId() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", state='" + getState() + "'" +
            ", zip='" + getZip() + "'" +
            "}";
    }

}
