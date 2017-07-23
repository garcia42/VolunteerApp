package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import io.realm.RealmObject;

public class GeoLocation extends RealmObject {

    String accuracy;
    double latitude;
    double longitude;

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}