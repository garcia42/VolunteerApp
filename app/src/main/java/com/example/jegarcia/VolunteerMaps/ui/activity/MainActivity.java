package com.example.jegarcia.VolunteerMaps.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.ui.RealmHelper;
import com.example.jegarcia.VolunteerMaps.ui.apiCall.DownloadOpportunitiesService;
import com.example.jegarcia.VolunteerMaps.ui.fragment.Map;

import org.apache.axis.utils.StringUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    private RealmConfiguration realmConfiguration;
    private DrawerLayout mDrawerLayout;

    private Realm realm;
    private String TAG = MainActivity.class.getSimpleName();
    private int PERMISSIONS_RESULT_CODE = 113441235;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.framelayout);
//        ButterKnife.bind(this);
        Log.d(TAG, "MainActivity onCreate");

        realm = getRealm();
        RealmHelper.removeOldEvents(this); //TODO do this less frequently

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_RESULT_CODE);
            Log.d(TAG, "MainActivity permission not allowed");
        } else {
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            getLocation(locManager, location);
        }
//            setupViewPager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.map_frame, new Map()).commit();
        setupNavigationDrawer();
    }

    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        SharedPreferences.Editor editor = getSharedPreferences("OPP_CATEGORY_ID", MODE_PRIVATE).edit();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        switch(menuItem.getItemId()) {
                            case R.id.all_menu:
                                editor.putInt("categoryId", 0);
                                break;
                            case R.id.advocacy_menu:
                                editor.putInt("categoryId", 23);
                                break;
                            case R.id.animals_menu:
                                editor.putInt("categoryId", 30);
                                break;
                            case R.id.arts_menu:
                                editor.putInt("categoryId", 34);
                                break;
                            case R.id.children_menu:
                                editor.putInt("categoryId", 22);
                                break;
                            case R.id.community_menu:
                                editor.putInt("categoryId", 25);
                                break;
                            case R.id.computers_menu:
                                editor.putInt("categoryId", 37);
                                break;
                            case R.id.education_menu:
                                editor.putInt("categoryId", 15);
                                break;
                            case R.id.health_menu:
                                editor.putInt("categoryId", 11);
                                break;
                            case R.id.seniors_menu:
                                editor.putInt("categoryId", 12);
                                break;
                            case R.id.other_menu:
                                editor.putInt("categoryId", -1);
                                break;
                        }
                        editor.apply();
                        Map mapFragment = (Map) getSupportFragmentManager().findFragmentById(R.id.map_frame);
                        mapFragment.updateCategoryId();
                        return true;
                    }
                });
    }

    private void getLocation(LocationManager locManager, Location location) {

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (network_enabled) {
            Log.d(TAG, "MainActivity network enabled");
        }

        double longitude;
        double latitude;
        String city;
        if (location != null) {
            Log.d(TAG, "MainActivity location mananger getLastKnownLocation " + location.toString());
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            city = RealmHelper.getCityFromPosition(this, longitude, latitude);
            if (StringUtils.isEmpty(city)) {
                city = "San Francisco";
            }
            Log.d(TAG, "Location of user " + longitude + " " + latitude + " " + city);
        } else {
            city = "Sacramento";
        }
        startService(prepareIntent(city));
//        VolunteerMatchApiService.downloadAllOppsInArea(0, daysSince, this, city);
    }

    private Intent prepareIntent(String city) {
        Intent localIntent = new Intent(this, DownloadOpportunitiesService.class);
        localIntent.putExtra("location", city);
        return localIntent;
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
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
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
        if (realm != null) {
            realm.close();
        }
        realm = null;
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
}