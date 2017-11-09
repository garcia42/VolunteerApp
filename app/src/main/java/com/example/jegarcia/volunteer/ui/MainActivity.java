package com.example.jegarcia.volunteer.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jegarcia.volunteer.R;
import com.example.jegarcia.volunteer.ui.volunteerMatchRecyclerView.SearchResultAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.example.jegarcia.volunteer.ui.VolunteerRequestUtils.LOCATION;

public class MainActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks {

    private static final String TAG = MainActivity.class.getName();
    RecyclerViewFragment volunteerListFragment;
    private RealmConfiguration realmConfiguration;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    NonSwipeViewPager mViewPager;
    private Realm realm;
    private String location;
    private static final int LOCATION_REQUEST_CODE = 123123;
    private VolunteerFragmentPagerAdapter mVolunteerFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        RealmHelper.removeOldEvents(this); //TODO do this less frequently

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mVolunteerFragmentPagerAdapter =
                new VolunteerFragmentPagerAdapter(
                        getSupportFragmentManager());
        mViewPager.setAdapter(mVolunteerFragmentPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissionList = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
                requestPermissions(permissionList, LOCATION_REQUEST_CODE);
                return;
            }
            this.location = findCityName();
        }

        if (savedInstanceState == null) {
//            volunteerListFragment = new RecyclerViewFragment();
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
                case (0):
                    fragment = new RecyclerViewFragment();
                    args.putString(LOCATION, location);
                    volunteerListFragment = (RecyclerViewFragment) fragment;
                    fragment.setArguments(args);
                    return fragment;
                case (1):
                    fragment = new Map();
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

    private String findCityName() {
        try {
            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                return RealmHelper.getCityFromPosition(this, location.getLongitude(), location.getLatitude());
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Tried to get location but didn't have permissions");
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findCityName();
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
