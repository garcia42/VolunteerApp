package com.example.jegarcia.volunteer.models;

import com.orm.SugarRecord;

/**
 * Created by jegarcia on 2/15/17.
 */

public class Location extends SugarRecord {

    private String city;
    private String country;
    private GeoLocation geoLocation;
    private String postalCode;
    private String region;
    private String street1;
    private String street2;
    private String street3;

    private class GeoLocation extends SugarRecord {
        private String accuracy;
        private long latitude;
        private long longitude;
    }
}
