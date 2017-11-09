package com.example.jegarcia.volunteer.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jegarcia.volunteer.R;
import com.example.jegarcia.volunteer.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.volunteer.ui.volunteerMatchRecyclerView.SearchResultAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.example.jegarcia.volunteer.ui.VolunteerRequestUtils.LOCATION;

public class Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = Map.class.getName();
    private static final float DEFAULT_ZOOM = 12.0f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private LatLng mDefaultLocation = new LatLng(38.5816, 121.4944);
    RealmResults<Opportunities> opportunities;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private HashMap<Marker, Integer> markers = new HashMap<>();
    private MapView mapView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "Begin onCreate");
        super.onCreate(savedInstanceState);

        // Do other setup activities here too, as described elsewhere in this tutorial.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();
        Log.i(TAG, "End onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.map_layout, container, false);

        mapView = (MapView) rootView.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "Begin onMapReady");
        mMap = googleMap;

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        mMap.setOnInfoWindowClickListener(this);

        //Create markers for opportunities
        loadOpportunitiesListener(googleMap);
    }

    @Override
    public void onStop() {
        super.onStop();
        opportunities.removeAllChangeListeners();
    }

    public void loadOpportunitiesListener(final GoogleMap googleMap) {
        Log.i(TAG, "Begin getOpportunities");
        Realm realm = ((MainActivity) getActivity()).getRealm();

        opportunities = realm.where(Opportunities.class).findAllAsync();

        opportunities.addChangeListener(new RealmChangeListener<RealmResults<Opportunities>>() {
            @Override
            public void onChange(RealmResults<Opportunities> opportunities) {
                Log.i(TAG, "begin onChangeListener");
//                googleMap.clear();
                for (Opportunities opportunity: opportunities) {
                    if (opportunity.getLocation().getGeoLocation() != null) {
                        LatLng latLng = new LatLng(opportunity.getLocation().getGeoLocation().getLatitude(),
                                opportunity.getLocation().getGeoLocation().getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .title(opportunity.getTitle());
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.setTag(opportunity.getOppId());
                    }
                }
                Log.i(TAG, "end onChangeListener");
            }
        });
        Log.i(TAG, "End getOpportunities");
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
     * Before getting the device location, you must check location
     * permission, as described earlier in the tutorial. Then:
     * Get the best and most recent location of the device, which may be
     * null in rare cases when a location is not available.
     */
        if (mLocationPermissionGranted) {
            mLastKnownLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "GoogleApiClient connected!");
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        b.putInt("opportunity_id", (int) marker.getTag());
        OpportunityFragment opportunityFragment = new OpportunityFragment();
        opportunityFragment.setArguments(b);
//        ft.replace(R.id.content_frame, opportunityFragment).addToBackStack(null).commit(); TODO make this an activity
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        getChildFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
    }

    public static class MainActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks {

        @BindView(R.id.tabLayout)
        TabLayout tabLayout;

        RecyclerViewFragment volunteerListFragment;
        private RealmConfiguration realmConfiguration;

        private Realm realm;
        private double longitude;
        private double latitude;
        private String city;
        private String TAG = MainActivity.class.getName();
        private int PERMISSIONS_RESULT_CODE = 15;
        private ViewPager mViewPager;
        private VolunteerFragmentPagerAdapter mVolunteerFragmentPagerAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);

            realm = Realm.getDefaultInstance();
            RealmHelper.removeOldEvents(this); //TODO do this less frequently

            // ViewPager and its adapters use support library
            // fragments, so use getSupportFragmentManager.
            mVolunteerFragmentPagerAdapter =
                    new VolunteerFragmentPagerAdapter(
                            getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(mVolunteerFragmentPagerAdapter);

            tabLayout.setupWithViewPager(mViewPager);

            Log.d(TAG, "MainActivity onCreate");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_RESULT_CODE);
                Log.d(TAG, "MainActivity permission not allowed");
                return;
            } else {
                Location location;
                LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                getLocation(locManager, location);
            }

            if (savedInstanceState == null) {
    //            volunteerListFragment = new RecyclerViewFragment();
    //            getIntent().putExtra(LOCATION, city);
    //            volunteerListFragment.setArguments(getIntent().getExtras());
    //            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    //            ft.add(R.id.content_frame, volunteerListFragment).commit();
            }
        }

        public class VolunteerFragmentPagerAdapter extends FragmentPagerAdapter {
            public VolunteerFragmentPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public Fragment getItem(int i) {
                Fragment fragment;
                Bundle args = new Bundle();
                switch (i) {
                    case 0:
                        fragment = new RecyclerViewFragment();
                        volunteerListFragment = (RecyclerViewFragment) fragment;
                        args.putString(LOCATION, city); // This needs current city
                        fragment.setArguments(args);
                        return fragment;
                    case 1:
                        fragment = new Map();
    //                    args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1);
                        fragment.setArguments(args);
                        return fragment;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "List" : "Map";
            }
        }

        private void getLocation(LocationManager locManager, Location location) {

            boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (network_enabled) {

                Log.d(TAG, "MainActivity network enabled");
            }

            Log.d(TAG, "MainActivity location mananger getLastKnownLocation " + location.toString());
            if (location != null) {
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
                }
            }
        }

        public SearchResultAdapter getAdapter() {
            return volunteerListFragment.getAdapter();
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

        public static boolean isEmulator() {
            return Build.FINGERPRINT.startsWith("generic")
                    || Build.FINGERPRINT.startsWith("unknown")
                    || Build.MODEL.contains("google_sdk")
                    || Build.MODEL.contains("Emulator")
                    || Build.MODEL.contains("Android SDK built for x86")
                    || Build.MANUFACTURER.contains("Genymotion")
                    || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                    || "google_sdk".equals(Build.PRODUCT);
        }

        @Override
        public void onPreExecute() {

        }

        @Override
        public void onProgressUpdate(int percent) {

        }

        @Override
        public void onCancelled() {

        }

        @Override
        public void onPostExecute() {

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
            return this.realm;
        }
    }
}
