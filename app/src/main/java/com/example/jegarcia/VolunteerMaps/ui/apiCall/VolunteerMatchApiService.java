package com.example.jegarcia.VolunteerMaps.ui.apiCall;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.VolunteerApplication;
import com.example.jegarcia.VolunteerMaps.models.restModels.OppSearchResult;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.GeoLocation;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.apache.axis.encoding.Base64;
import org.apache.axis.utils.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.daysSince;

public class VolunteerMatchApiService {

    private static final DateFormat DATETIME_FORMAT =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String CHARSET = "UTF-8";
    private static final String apiUrl = "http://www.volunteermatch.org/api/call";

    private static final String ACCOUNT_NAME = "garciaj42"; //TODO remove this from the app
    private static final String PASSWORD = "0ed901afd6584a580e3aaf55484dec04";

    private static final String HTTP_METHOD_GET = "GET";
    private static final String TAG = VolunteerMatchApiService.class.getName() + "Jesus";
    private static final String PREFS_NAME = "volunteerPrefsConfig";

    private static Gson gson;

    private static byte[] generateNonce() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte nonce[] = new byte[20];
            random.nextBytes(nonce);
            return nonce;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to generate nonce", e);
        }
        return null;
    }

    private static byte[] sha256(byte[] payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            return digest.digest(payload);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to generate SHA256 hash", e);
        }
        return null;
    }

    private static WSSECredentials buildWSSECredentials(String accountName, String password) {
        WSSECredentials wsse = new WSSECredentials();
        wsse.userName = accountName;
        byte [] nonce = generateNonce();
        if (nonce == null) {
            Log.e(TAG, "Failed to generate nonce");
            return null;
        }

        wsse.nonce = Base64.encode(nonce);
        wsse.timestamp = DATETIME_FORMAT.format(new Date(System.currentTimeMillis()));

        String digestInput = wsse.nonce + wsse.timestamp + password;
        wsse.passwordDigest = Base64.encode(sha256(digestInput.getBytes()));

        return wsse;
    }

    private static HashMap<String, String> buildMap(WSSECredentials wsse) {

        String credentials = "UsernameToken Username=\"" + ACCOUNT_NAME + "\", " +
                "PasswordDigest=\"" + wsse.passwordDigest + "\", " +
                "Nonce=\"" + wsse.nonce + "\", " +
                "Created=\"" + wsse.timestamp + "\", ";

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Accept-Charset", CHARSET);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "WSSE profile=\"UsernameToken\"");
//        Log.d(TAG, "Sending request with credentials = " + credentials);
        headers.put("X-WSSE", credentials);
        return headers;
    }

    private static ConnectionInfo createConnectionInfo(WSSECredentials wsse, String url, String apiMethod, String query, String httpMethod) {
        StringBuilder q = new StringBuilder();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        try {
            q.append("action=").append(URLEncoder.encode(apiMethod, CHARSET));
            q.append("&query=").append(URLEncoder.encode(query, CHARSET));
            connectionInfo.headers = buildMap(wsse);
            connectionInfo.url = url + "?" + q.toString();
            return connectionInfo;
        } catch (Exception e) {
            Log.e(TAG, "An unknown error occurred while processing an API call for method " + apiMethod + ", query " + query);
        }
        return null;
    }

    public static void downloadAllOppsInArea(int pageNumber, int daysSince, Context context, String location) {
        SharedPreferences editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String key = context.getString(R.string.last_check_date) + location; //Takes into account location
        String updatedSince = editor.getString(key, VolunteerRequestUtils.formatDateAndTime(15));
        String searchOppsQuery = SearchOpportunitiesExample.buildSearchOppsQuery(pageNumber, updatedSince, daysSince, location);
        try {
            VolunteerMatchApiService.WSSECredentials wsse = buildWSSECredentials(ACCOUNT_NAME, PASSWORD);
            VolunteerMatchApiService.ConnectionInfo connectionInfo =
                    VolunteerMatchApiService.createConnectionInfo(wsse, apiUrl, SearchOpportunitiesExample.SEARCH_OPPORTUNITIES, searchOppsQuery, HTTP_METHOD_GET);
            createJsonObjectRequest(connectionInfo.url, connectionInfo.headers, location, context);
        } catch (Exception e) {
            Log.e(TAG, "Unable to create WSSE credentials and createConnectionInfo");
            e.printStackTrace();
        }
    }

    private static void createJsonObjectRequest(final String url, final HashMap<String, String> headers, final String location, final Context context) {
        DownloadOpportunitiesRequest jsonObjReq = new DownloadOpportunitiesRequest(Request.Method.GET, url, headers, context, location, getGson());
        // Adding request to request queue
        VolunteerApplication.getInstance().addToRequestQueue(jsonObjReq, "");
    }

    private static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    static void enqueueOtherPages(OppSearchResult result, Context context, String location) {
        for (int i = 2; i < result.getResultsSize()/20; i++) {
            downloadAllOppsInArea(i, daysSince, context, location);
        }
    }

    static class ConnectionInfo {
        HashMap<String, String> headers;
        String url;
    }

    /**
     * Structure representing a set of WSSE credentials.
     */
    static class WSSECredentials {
        String userName= "";
        String passwordDigest= "";
        String nonce= "";
        String timestamp= "";
    }

    static void saveOpportunitiesAndGetData(final List<Opportunities> opportunities, Context context, String location) {
        storeOpportunities(opportunities);
        storeExtraData(opportunities, context, location);
    }

    private static void storeExtraData(final List<Opportunities> opportunities, final Context context, final String location) {
        try (Realm realmConfig = Realm.getDefaultInstance()) {
            realmConfig.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(@NonNull Realm realm) {
                    for (Opportunities opportunity : opportunities) {
                        String zip = opportunity.getLocation().getPostalCode();
                        LatLng zipLocation = getLatLngFromZip(zip, context);
                        if (zipLocation != null && opportunity.getLocation().getGeoLocation() == null) { //Geo location
                            opportunity.getLocation().setGeoLocation(new GeoLocation());
                            opportunity.getLocation().getGeoLocation().setLatitude(zipLocation.latitude);
                            opportunity.getLocation().getGeoLocation().setLongitude(zipLocation.longitude);
//                        opportunity.getLocation().getGeoLocation().setLongitude(Double.valueOf(zipLocation.longitude).longValue());
                        }
                        if (StringUtils.isEmpty(opportunity.getAvailability().getStartDate()) &&
                                StringUtils.isEmpty(opportunity.getAvailability().getEndDate())) { //End Date
                            opportunity.getAvailability().setEndDate(VolunteerRequestUtils.formatDate(-7));
                        }
                    }
                    realm.copyToRealmOrUpdate(opportunities);
                }
            });
        }
    }

    private static void storeOpportunities(final List<Opportunities> opportunities) {
        try (Realm realmConfig = Realm.getDefaultInstance()) {
            realmConfig.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(@NonNull Realm realm) {
                    for (Opportunities opportunity : opportunities) {
                        Opportunities user = realm.where(Opportunities.class).equalTo("id", opportunity.getOppId()).findFirst();
                        //Current Opportunity doesn't have an updated value and the one in the db exists but doesn't have an updated date either
                        if (user == null || StringUtils.isEmpty(user.getUpdated())) {
                            if (StringUtils.isEmpty(opportunity.getUpdated())) {
                                opportunity.setUpdated(VolunteerRequestUtils.formatDate(0)); //Today
                            }
                        }
                    }
                    realm.copyToRealmOrUpdate(opportunities);
                }
            });
        }
    }

    private static LatLng getLatLngFromZip(String zip, Context context) {
        final Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(zip, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            // handle exception
            Log.e(TAG, e.toString());
        }
        return null;
    }
}
