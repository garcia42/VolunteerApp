package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import io.realm.RealmObject;

public class GeoLocation extends RealmObject {

    String accuracy;
    long latitude;
    long longitude;

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}