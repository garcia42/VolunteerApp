package com.example.jegarcia.VolunteerMaps.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

class RealmHelper {

    static void removeOldEvents(Context context) {
        Realm realmConfig = Realm.getDefaultInstance();
//        Realm realmConfig = ((MainActivity) context).getRealm();
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

    private static LatLng getLatLngFromZip(String zip, Context context) {
        final Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(zip, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(context, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
        return null;
    }

    public static String getCityFromPosition(Context context, double longitude, double latitude) {
        Geocoder geoCoder = new Geocoder(context, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
            builder.append(address.get(0).getLocality());
        } catch (IOException e) {

        } catch (NullPointerException e) {

        }
        return builder.toString();
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
