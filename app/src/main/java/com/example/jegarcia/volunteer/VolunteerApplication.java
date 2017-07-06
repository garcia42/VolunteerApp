package com.example.jegarcia.volunteer;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import io.realm.Realm;

public class VolunteerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Realm
        Realm.init(this);

        JodaTimeAndroid.init(this);
    }
}
