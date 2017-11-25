package com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels;

import io.realm.RealmObject;

public class Availability extends RealmObject {

    String endDate;
    String endTime;
    boolean ongoing;
    boolean singleDayOpportunity;
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

    public boolean getOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public boolean getSingleDayOpportunity() {
        return singleDayOpportunity;
    }

    public void setSingleDayOpportunity(boolean singleDayOpportunity) {
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
