package com.example.jegarcia.volunteer;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.example.jegarcia.volunteer.models.volunteerMatchModels.GeoLocation;
import com.example.jegarcia.volunteer.models.volunteerMatchModels.Opportunities;
import com.google.android.gms.maps.model.LatLng;

import org.apache.axis.utils.StringUtils;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

class RealmHelper {

    static void saveOpportunitiesAndGetData(final List<Opportunities> opportunities, Context context) {
        storeOpportunities(opportunities, context);
        storeLatLongs(opportunities, context);
    }

    private static void storeLatLongs(final List<Opportunities> opportunities, final Context context) {
        Realm realmConfig = ((MainActivity) context).getRealm();

        realmConfig.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                for (Opportunities opportunity : opportunities) {
                    String zip = opportunity.getLocation().getPostalCode();
                    LatLng zipLocation = getLatLngFromZip(zip, context);
                    if (zipLocation != null && opportunity.getLocation().getGeoLocation() == null) {
                        opportunity.getLocation().setGeoLocation(new GeoLocation());
                        opportunity.getLocation().getGeoLocation().setLatitude(zipLocation.latitude);
                        opportunity.getLocation().getGeoLocation().setLongitude(zipLocation.longitude);
//                        opportunity.getLocation().getGeoLocation().setLongitude(Double.valueOf(zipLocation.longitude).longValue());
                        realm.copyToRealmOrUpdate(opportunity);
                    }
                }
            }
        });
    }

    static void storeOpportunities(final List<Opportunities> opportunities, Context context) {
        Realm realmConfig = ((MainActivity) context).getRealm();
        realmConfig.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {
                for (Opportunities opportunity : opportunities) {
                    Opportunities user = realm.where(Opportunities.class).equalTo("id", opportunity.getOppId()).findFirst();
                    //Current Opportunity doesn't have an updated value and the one in the db exists but doesn't have an updated date either
                    if (user == null || StringUtils.isEmpty(user.getUpdated())) {
                        if (StringUtils.isEmpty(opportunity.getUpdated())) {
                            opportunity.setUpdated(VolunteerRequestUtils.formatDate(0)); //Today
                        }
                    }
                    realm.copyToRealmOrUpdate(opportunity);
                }
            }
        });
    }

    static void removeOldEvents(Context context) {
        Realm realmConfig = ((MainActivity) context).getRealm();
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
}
