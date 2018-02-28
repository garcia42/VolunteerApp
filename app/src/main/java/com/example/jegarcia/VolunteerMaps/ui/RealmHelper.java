package com.example.jegarcia.VolunteerMaps.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils;

import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

public class RealmHelper {

    public static void removeOldEvents(Context context) {
        try (Realm realmConfig = Realm.getDefaultInstance()) {
            realmConfig.executeTransactionAsync(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    RealmResults<Opportunities> opportunities = realm.where(Opportunities.class).findAll();
                    for (Opportunities opportunity : opportunities) {
                        if (VolunteerRequestUtils.isExpiredOpportunity(opportunity)) {
                            opportunity.deleteFromRealm();
                        }
                    }
                }
            });
        }
    }

    public static String getCityFromPosition(Context context, double longitude, double latitude) {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
            builder.append(address.get(0).getLocality());
        } catch (Exception e) {
            Log.d(TAG, "No Address Found to get city from");
            return null;
        }
        String city = builder.toString();
        if (city.toLowerCase().trim().equals("null")) {
            return null;
        }
        return city;
    }

    public static boolean hasOpportunitiesNearby(double latitude, double longitude) {
        Realm realmConfig = Realm.getDefaultInstance();
//        Realm realmConfig = ((MainActivity) context).getRealm();
        double box = RealmHelper.getBoundingLimits();
        Opportunities opportunitiesRealmResults = realmConfig
                .where(Opportunities.class)
                .greaterThan("location.geoLocation.latitude", latitude - box)
                .lessThan("location.geoLocation.latitude", latitude + box)
                .greaterThan("location.geoLocation.longitude", longitude - box)
                .lessThan("location.geoLocation.longitude", longitude + box)
                .findFirst();
        return opportunitiesRealmResults != null;
    }

    public static double getBoundingLimits() {
        // VolunteerMatch API defaults to ~32km away
        return 0.0089982311916 * 40;
    }
}
