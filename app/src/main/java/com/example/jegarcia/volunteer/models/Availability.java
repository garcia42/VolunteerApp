package com.example.jegarcia.volunteer.models;

import com.orm.SugarRecord;

public class Availability extends SugarRecord {

    private String endDate;
    private String endTime;
    private String ongoing;
    private String singleDayOpportunity;
    private String startDate;
    private String startTime;

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
