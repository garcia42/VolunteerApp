package com.example.jegarcia.VolunteerMaps.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.VolunteerMaps.ui.MapStateManager;
import com.example.jegarcia.VolunteerMaps.ui.RealmHelper;
import com.example.jegarcia.VolunteerMaps.ui.activity.MainActivity;
import com.example.jegarcia.VolunteerMaps.ui.activity.OpportunityActivity;
import com.example.jegarcia.VolunteerMaps.ui.apiCall.DownloadOpportunitiesService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.redoSearch)
    Button mRedoSearch;

    @BindView(R.id.map_view)
    MapView mapView;

    @BindView(R.id.transparentPage)
    LinearLayout transparentPage;

    private static final String TAG = Map.class.getSimpleName() + "Jesus";
    private static final float DEFAULT_ZOOM = 11.0f;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation = new LatLng(38.5816, 121.4944);
    RealmResults<Opportunities> opportunities;
    Set<Integer> opportunityIds = new HashSet<>();
    Set<LatLng> opportunityLocations = new HashSet<>();
    private static final String PREFS_NAME = "Volunteer_App_Shared_Preferences";

    // Declare a variable for the cluster manager.
    private ClusterManager<VolunteerClusterItem> mClusterManager;
    private int mCategoryId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        if (getActivity() != null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity() /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_layout, container, false);
        ButterKnife.bind(this, rootView);

        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final SharedPreferences pref = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!pref.getBoolean("HAS_DISMISSED_MAP_DIALOG", false)) {
            transparentPage.setVisibility(View.VISIBLE);
            transparentPage.bringToFront();
            transparentPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    pref.edit().putBoolean("HAS_DISMISSED_MAP_DIALOG", true).apply();
                }
            });
        }

        mapView.getMapAsync(this);
        return rootView;
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        if (mClusterManager == null && getContext() != null) {
            mClusterManager = new ClusterManager<> (getContext(), mMap);
            mClusterManager.setRenderer(new InfoMarkerRenderer(getContext(), mMap, mClusterManager));
        }

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
//        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterItemInfoWindowClickListener(mClusterItemInfoWindowClickListener);
    }

    public void updateCategoryId(int categoryId) {
        mCategoryId = categoryId;
        loadOpportunitiesListener();
    }

    public class InfoMarkerRenderer extends DefaultClusterRenderer<VolunteerClusterItem> {

        InfoMarkerRenderer(Context context, GoogleMap map, ClusterManager<VolunteerClusterItem> clusterManager) {
            super(context, map, clusterManager);
            //constructor
        }

        @Override
        protected void onBeforeClusterItemRendered(final VolunteerClusterItem infomarker, MarkerOptions markerOptions) {
            // you can change marker options
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 10; // if markers <=5 then not clustering
        }
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
        if (opportunities != null) {
            opportunities.removeAllChangeListeners();
        }
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            MapStateManager msm = new MapStateManager(getContext());
            msm.saveMapState(mMap);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "Begin onMapReady");
        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    public void loadOpportunitiesListener() {
        Log.i(TAG, "Create Realm Listener for new map events");
        final Realm realm = ((MainActivity) getActivity()).getRealm();
        mClusterManager.clearItems();
        opportunities = null;
        opportunityIds.clear();
        opportunityLocations.clear();
        if (mCategoryId > -1) { // Have a category to search for
            opportunities = realm.where(Opportunities.class).contains("categoryIds", "," + mCategoryId + ",").findAllAsync();
        } else { //Only show saved opportunities
            opportunities = realm.where(Opportunities.class).equalTo("isLiked", true).findAllAsync();
        }
        opportunities.addChangeListener(new RealmChangeListener<RealmResults<Opportunities>>() {
            @Override
            public void onChange(@NonNull final RealmResults<Opportunities> opportunities) {
                Log.i(TAG, "In Change Listener for Map Markers: isLoaded: " + opportunities.isLoaded() + " Size: " + opportunities.size());
                new ProcessMarkersTask().execute(realm.copyFromRealm(opportunities));
            }
        });
    }

    class ProcessMarkersTask extends AsyncTask<List<Opportunities>, Void, List<VolunteerClusterItem>> {

        @Override
        protected final List<VolunteerClusterItem> doInBackground(List<Opportunities>... args) {
            List<Opportunities> opportunities = args[0];
            List<VolunteerClusterItem> clusterItems = new ArrayList<>();
            for (Opportunities opportunity: opportunities) {
                boolean isSaved = opportunity.isLiked() && mCategoryId == -1;
                boolean categoryMatch = opportunity.getCategoryIds().contains(String.valueOf(mCategoryId));
                if (opportunity.getLocation().getGeoLocation() != null
                        && !opportunityIds.contains(opportunity.getOppId())
                        && (isSaved || categoryMatch)) {
                    LatLng latLng = new LatLng(opportunity.getLocation().getGeoLocation().getLatitude(),
                            opportunity.getLocation().getGeoLocation().getLongitude());
                    while (opportunityLocations.contains(latLng)) {
                        latLng = moveNearbyRandomly(latLng);
                    }
                    opportunityLocations.add(latLng);
                    opportunityIds.add(opportunity.getOppId());
                    VolunteerClusterItem offsetItem =
                            new VolunteerClusterItem(opportunity.getOppId(), latLng.latitude, latLng.longitude, opportunity.getTitle(), opportunity.getParentOrg().getName());
                    clusterItems.add(offsetItem);
                }
            }
            return clusterItems;
        }

        @Override
        protected void onPostExecute(List<VolunteerClusterItem> clusterItems) {
            super.onPostExecute(clusterItems);
            mClusterManager.addItems(clusterItems);
            Map.this.mClusterManager.cluster();
        }
    }

    private LatLng moveNearbyRandomly(LatLng latLng) {
        Random rand = new Random();
        LatLng other = null;
        int size = opportunityLocations.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(LatLng obj : opportunityLocations) {
            if (i == item)
                other = obj;
            i++;
        }
        double heading = SphericalUtil.computeHeading(latLng, other);
        return SphericalUtil.computeOffset(latLng, rand.nextInt(80) + 70, heading);
    }

    ClusterManager.OnClusterItemInfoWindowClickListener<VolunteerClusterItem> mClusterItemInfoWindowClickListener = new ClusterManager.OnClusterItemInfoWindowClickListener<VolunteerClusterItem>() {
        @Override
        public void onClusterItemInfoWindowClick(VolunteerClusterItem clusterItem) {
            Intent intent = new Intent(getActivity(), OpportunityActivity.class);
            intent.putExtra("opportunity_id", clusterItem.getTag());
            startActivity(intent);
        }
    };

    private void getDeviceLocation() {
        Log.i(TAG, "Updating map Location, getDeviceLocation");
        Context context = getActivity();
        if (context == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        MapStateManager msm = new MapStateManager(getActivity());
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

        //Should just save map config in map state manager, not in saved instance state.
        //Should reset shared pref when view is destroyed
        if (msm.getSavedCameraPosition() != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(msm.getSavedCameraPosition());
            mMap.moveCamera(update);
            mMap.setMapType(msm.getSavedMapType());
        // Set the map's camera position to the current location of the device.
        } else if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            msm.saveMapState(mMap);
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private Intent prepareIntent(String city) {
        Intent localIntent = new Intent(getActivity(), DownloadOpportunitiesService.class);
        localIntent.putExtra("location", city);
        return localIntent;
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Get the current location of the device and set the position of the map.
        //Create markers for opportunities
        if (mMap != null && getContext() != null) {
            setUpClusterer();
            loadOpportunitiesListener();
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    mClusterManager.onCameraIdle();
                    MapStateManager msm = new MapStateManager(getContext());
                    if (msm.getSavedCameraPosition() != null && mMap.getCameraPosition() != null) {
                        if (SphericalUtil.computeDistanceBetween(msm.getSavedCameraPosition().target, mMap.getCameraPosition().target) > 9000) {
                            //Make button visible
                            mRedoSearch.setVisibility(View.VISIBLE);
                            mRedoSearch.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    double latitude = mMap.getCameraPosition().target.latitude;
                                    double longitude = mMap.getCameraPosition().target.longitude;
                                    String city = RealmHelper.getCityFromPosition(getContext(), longitude, latitude);
                                    if (!StringUtils.isEmpty(city)) {
                                        Log.d(TAG, "Redo Search in city: " + city);
                                        Context context = Map.this.getActivity();
                                        if (context != null) {
                                            context.startService(prepareIntent(city));
                                        } else {
                                            Log.e(TAG, "Failed to start search service in Map");
                                        }
                                        mRedoSearch.setOnClickListener(null);
                                        mRedoSearch.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Searching in " + city, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "No city found in this area", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        MapStateManager mapStateManager = new MapStateManager(getContext());
        mapStateManager.eraseMapState();
    }

    public ClusterManager.OnClusterItemClickListener<VolunteerClusterItem> mClusterItemClickListener = new ClusterManager.OnClusterItemClickListener<VolunteerClusterItem>() {

        @Override
        public boolean onClusterItemClick(VolunteerClusterItem volunteerClusterItem) {
            Intent intent = new Intent(getActivity(), OpportunityActivity.class);
            intent.putExtra("opportunity_id", volunteerClusterItem.getTag());
            startActivity(intent);
            return true;
        }

    };

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class VolunteerClusterItem implements ClusterItem {
        private LatLng mPosition;
        private String mTitle;
        private String mSnippet;
        private int mTag;

        VolunteerClusterItem(int oppId, double lat, double lng, String title, String snippet) {
            mTag = oppId;
            mPosition = new LatLng(lat, lng);
            mTitle = title;
            mSnippet = snippet;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSnippet() {
            return mSnippet;
        }

        public int getTag() {
            return mTag;
        }

        public void setTag(int mTag) {
            this.mTag = mTag;
        }
    }
}
