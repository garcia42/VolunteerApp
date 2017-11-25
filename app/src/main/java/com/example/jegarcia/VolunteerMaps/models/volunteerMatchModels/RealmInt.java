package com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels;

import io.realm.RealmObject;

/**
 * Created by Jesus on 7/3/2017.
 */

public class RealmInt extends RealmObject {
    public int inty;

    public int getInty() {
        return inty;
    }

    public void setInty(int value) {
        inty = value;
    }
}
