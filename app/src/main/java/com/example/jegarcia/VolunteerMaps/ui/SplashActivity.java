package com.example.jegarcia.VolunteerMaps.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by garciaj42 on 11/13/17.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSIONS_RESULT_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Location location;
//                LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//                try {
//                    location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                    getLocation(locManager, location);
//                } catch (SecurityException e) {
//                    Log.e(TAG, "Permission For Location Not Granted");
//                }
//                RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) mVolunteerFragmentPagerAdapter.getItem(0);
//                recyclerViewFragment.invokeTaskFragment(0, daysSince);
//            }
//        }
//    }
}
