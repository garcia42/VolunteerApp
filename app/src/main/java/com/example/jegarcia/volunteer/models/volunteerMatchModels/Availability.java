package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import com.example.jegarcia.volunteer.models.BaseObjectModel;

import java.io.Serializable;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;

@Entity
public abstract class Availability extends BaseObjectModel implements Serializable, Persistable {

    @Key
    @Generated
    int id;

    String endDate;
    String endTime;
    String ongoing;
    String singleDayOpportunity;
    String startDate;
    String startTime;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOngoing() {
        return ongoing;
    }

    public void setOngoing(String ongoing) {
        this.ongoing = ongoing;
    }

    public String getSingleDayOpportunity() {
        return singleDayOpportunity;
    }

    public void setSingleDayOpportunity(String singleDayOpportunity) {
        this.singleDayOpportunity = singleDayOpportunity;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
