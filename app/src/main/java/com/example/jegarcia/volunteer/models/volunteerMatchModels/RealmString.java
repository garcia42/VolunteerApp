package com.example.jegarcia.volunteer.models.volunteerMatchModels;

import io.realm.RealmObject;

public class RealmString extends RealmObject {
    public String string;

    public RealmString() {
    }

    public RealmString(String s) {
        string = s;
    }

    public String getString() {
        return string;
    }

    public void setString(String value) {
        string = value;
    }
}
