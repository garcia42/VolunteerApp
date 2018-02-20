package com.example.jegarcia.VolunteerMaps.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.ui.RealmHelper;
import com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerMatchApiService;
import com.example.jegarcia.VolunteerMaps.ui.fragment.Map;
import com.example.jegarcia.VolunteerMaps.ui.fragment.RecyclerViewFragment;

import org.apache.axis.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.IS_SAVED;
import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.LATITUDE;
import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.LOCATION;
import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.LONGITUDE;
import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.daysSince;

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
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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
            if (StringUtils.isEmpty(city)) {
                city = "San Francisco";
            }
            Log.d(TAG, "Location of user " + longitude + " " + latitude + " " + city);
        } else {
            latitude = 38.57179019;
            longitude = -121.47857666;
            city = "Sacramento";
        }
        VolunteerMatchApiService.downloadAllOppsInArea(0, daysSince, this, city);
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
                setupViewPager();
            }
        }
    }

    private void setupViewPager() {
        VolunteerFragmentPagerAdapter mVolunteerFragmentPagerAdapter = new VolunteerFragmentPagerAdapter(
                getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mVolunteerFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(2); //For all three tabs
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void showRecyclerViewLoadingIcon() {
        ViewPager mViewPager = findViewById(R.id.pager);
        VolunteerFragmentPagerAdapter adapter = (VolunteerFragmentPagerAdapter) mViewPager.getAdapter();
        if (adapter != null) {
            ((RecyclerViewFragment) adapter.getFragment(1)).showLoadingIcon();
        }
    }

    public void hideRecyclerViewLoadingIcon() {
        ViewPager mViewPager = findViewById(R.id.pager);
        VolunteerFragmentPagerAdapter adapter = (VolunteerFragmentPagerAdapter) mViewPager.getAdapter();
        if (adapter != null) {
            ((RecyclerViewFragment) adapter.getFragment(1)).stopLoadingIcon();
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
}