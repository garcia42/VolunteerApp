package com.example.jegarcia.VolunteerMaps.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.VolunteerApplication;
import com.example.jegarcia.VolunteerMaps.models.restModels.OppSearchResult;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.GeoLocation;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.google.android.gms.maps.model.LatLng;

import org.apache.axis.utils.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.jegarcia.VolunteerMaps.ui.VolunteerMatchApiService.ACCOUNT_NAME;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerMatchApiService.PASSWORD;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerMatchApiService.apiUrl;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerMatchApiService.buildWSSECredentials;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.IS_SAVED;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.LATITUDE;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.LOCATION;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.LONGITUDE;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.formatDateAndTime;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout)
        TabLayout tabLayout;

    RecyclerViewFragment volunteerListFragment;
    private RealmConfiguration realmConfiguration;

    private Realm realm;
    private double longitude;
    private double latitude;
    private String city;
    private String TAG = MainActivity.class.getSimpleName();
    private int PERMISSIONS_RESULT_CODE = 113441235;
    private ViewPager mViewPager;
    private VolunteerFragmentPagerAdapter mVolunteerFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "MainActivity onCreate");

        realm = Realm.getDefaultInstance();
        RealmHelper.removeOldEvents(this); //TODO do this less frequently

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_RESULT_CODE);
            Log.d(TAG, "MainActivity permission not allowed");
        } else {
            Location location;
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            getLocation(locManager, location);
            setupViewPager();
        }
    }

    private class VolunteerFragmentPagerAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> fragments = new SparseArray<>();

        VolunteerFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            if (fragments.get(i) != null) {
                return fragments.get(i);
            }

            Fragment fragment;
            Bundle args = new Bundle();
            switch (i) {
                case 0:
                    fragment = new Map();
                    fragment.setArguments(args);
                    return fragment;
                case 1:
                    fragment = new RecyclerViewFragment();
                    volunteerListFragment = (RecyclerViewFragment) fragment;
                    args.putString(LOCATION, city); // This needs current city
                    args.putDouble(LATITUDE, latitude);
                    args.putDouble(LONGITUDE, longitude);
                    args.putBoolean(IS_SAVED, false);
                    fragment.setArguments(args);
                    return fragment;
                case 2:
                    fragment = new RecyclerViewFragment();
                    args.putBoolean(IS_SAVED, true);
                    fragment.setArguments(args);
                    return fragment;
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            fragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Map";
                case 1:
                    return "List";
                case 2:
                    return "Saved";
            }
            return "Volunteer";
        }
    }

    private void getLocation(LocationManager locManager, Location location) {

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (network_enabled) {
            Log.d(TAG, "MainActivity network enabled");
        }

        if (location != null) {
            Log.d(TAG, "MainActivity location mananger getLastKnownLocation " + location.toString());
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            city = RealmHelper.getCityFromPosition(this, longitude, latitude);
            Log.d(TAG, "Location of user " + longitude + " " + latitude + " " + city);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_RESULT_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location location;
                LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                try {
                    location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    getLocation(locManager, location);
                } catch (SecurityException e) {
                    Log.e(TAG, "Permission For Location Not Granted");
                }
                // ViewPager and its adapters use support library
                // fragments, so use getSupportFragmentManager.
                setupViewPager();
            }
        }
    }

    private void setupViewPager() {
        mVolunteerFragmentPagerAdapter =
                new VolunteerFragmentPagerAdapter(
                        getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mVolunteerFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(2); //For all three tabs
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void invokeTaskFragment(int pageNumber, int daysSince, Context context, String location) {
        String updatedSince = formatDateAndTime(daysSince);
        String searchOppsQuery = SearchOpportunitiesExample.buildSearchOppsQuery(pageNumber, updatedSince, daysSince, location);
//        mTaskFragment.updateData(VolunteerMatchApiService.HTTP_METHOD_GET, searchOppsQuery, SearchOpportunitiesExample.SEARCH_OPPORTUNITIES, location);
        startVolleyRequest(VolunteerMatchApiService.HTTP_METHOD_GET, searchOppsQuery, SearchOpportunitiesExample.SEARCH_OPPORTUNITIES, location);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public RealmConfiguration getRealmConfig() {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
        }
        return realmConfiguration;
    }

    public void resetRealm() {
        Realm.deleteRealm(getRealmConfig());
    }

    public Realm getRealm() {
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        return this.realm;
    }

    public void startVolleyRequest(String httpMethod,
                                   String searchQuery,
                                   String restMethod,
                                   String location) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        VolunteerMatchApiService.WSSECredentials wsse = null;
        try {
            wsse = buildWSSECredentials(ACCOUNT_NAME, PASSWORD);
            VolunteerMatchApiService.ConnectionInfo connectionInfo =
                    VolunteerMatchApiService.createConnectionInfo(wsse, apiUrl, restMethod, searchQuery, httpMethod);
            Cache cache = VolunteerApplication.getInstance().getRequestQueue().getCache();
            if(cache.get(connectionInfo.url) == null) { // Redo if null, otherwise don't
                // Cached response doesn't exists. Make network call here
                createJsonObjectRequest(connectionInfo.url, connectionInfo.headers, location);
                RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) mVolunteerFragmentPagerAdapter.getFragment(1);
                if (recyclerViewFragment != null) {
                    recyclerViewFragment.showLoadingIcon();
                }
            } else {
                Log.d(TAG, "Cache Hit, No need to download");
                RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) mVolunteerFragmentPagerAdapter.getFragment(1);
                if (recyclerViewFragment != null && VolunteerApplication.getInstance().getRequestsRemaining() == 0) { //Stop the scroll listener load
                    recyclerViewFragment.stopLoadingIcon();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to create WSSE credentials and createConnectionInfo");
            e.printStackTrace();
        }
    }

    private void createJsonObjectRequest(String url, final HashMap<String, String> headers, final String location) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        OppSearchResult result = SearchOpportunitiesExample.parseResult(response.toString());
                        if (result == null) {
                            Log.e(TAG, "No internet connection, or couldn't communicate with server");
                            return;
                        }
                        if (result.getOpportunities() == null) {
                            Log.d(TAG, "No opportunities in this area?");
                            return;
                        }
                        Log.d(TAG, "Finished searching for opportunities size: " + result.getOpportunities().size());
                        Log.d(TAG, "Finished searching for currentPage: " + result.getCurrentPage());
                        saveOpportunitiesAndGetData(result.getOpportunities(), getBaseContext(), location);
                        VolunteerApplication.getInstance().decrementRequestsRemaining();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

        };

        // Adding request to request queue
        VolunteerApplication.getInstance().addToRequestQueue(jsonObjReq, "");
    }

    private void saveOpportunitiesAndGetData(final List<Opportunities> opportunities, Context context, String location) {
        storeOpportunities(opportunities, context);
        storeExtraData(opportunities, context, location);
    }

    private void storeExtraData(final List<Opportunities> opportunities, final Context context, final String location) {
        Realm realmConfig = Realm.getDefaultInstance();
//        Realm realmConfig = ((MainActivity) context).getRealm();

        realmConfig.executeTransactionAsync(new Realm.Transaction() {

            @Override
            public void execute(Realm realm) {

                for (Opportunities opportunity : opportunities) {
                    String zip = opportunity.getLocation().getPostalCode();
                    LatLng zipLocation = getLatLngFromZip(zip, context);
                    if (zipLocation != null && opportunity.getLocation().getGeoLocation() == null) { //Geo location
                        opportunity.getLocation().setGeoLocation(new GeoLocation());
                        opportunity.getLocation().getGeoLocation().setLatitude(zipLocation.latitude);
                        opportunity.getLocation().getGeoLocation().setLongitude(zipLocation.longitude);
                        Log.d(TAG, "LATITUDE: " + latitude + " LONGITUDE: " + longitude);
//                        opportunity.getLocation().getGeoLocation().setLongitude(Double.valueOf(zipLocation.longitude).longValue());
                    }
                    if (StringUtils.isEmpty(opportunity.getAvailability().getStartDate()) &&
                            StringUtils.isEmpty(opportunity.getAvailability().getEndDate())) { //End Date
                        opportunity.getAvailability().setEndDate(VolunteerRequestUtils.formatDate(-7));
                    }
                }
                realm.copyToRealmOrUpdate(opportunities);
            }
        }, new Realm.Transaction.OnSuccess() {

            @Override
            public void onSuccess() {
                //TODO turn off loading for scroll listener and turn off the loading animation
                RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) mVolunteerFragmentPagerAdapter.getFragment(1);
                Log.d(TAG, "Requests Remaining: " + VolunteerApplication.getInstance().getRequestsRemaining());
                if (recyclerViewFragment != null && VolunteerApplication.getInstance().getRequestsRemaining() == 0) {
                    recyclerViewFragment.stopLoadingIcon();
                }
            }
        });
    }

    private void storeOpportunities(final List<Opportunities> opportunities, Context context) {
        Realm realmConfig = Realm.getDefaultInstance();
//        Realm realmConfig = ((MainActivity) context).getRealm();
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
                }
                realm.copyToRealmOrUpdate(opportunities);
            }
        });
    }

    private LatLng getLatLngFromZip(String zip, Context context) {
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