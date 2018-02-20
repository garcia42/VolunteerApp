package com.example.jegarcia.VolunteerMaps.ui.apiCall;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jegarcia.VolunteerMaps.VolunteerApplication;
import com.example.jegarcia.VolunteerMaps.models.restModels.OppSearchResult;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.VolunteerMaps.ui.activity.MainActivity;

import org.json.JSONObject;

import java.util.Map;

import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerMatchApiService.saveOpportunitiesAndGetData;

public class DownloadOpportunitiesRequest extends JsonObjectRequest {

    private static final String TAG = DownloadOpportunitiesRequest.class.getSimpleName();
    private Map<String, String> mHeaders;

    public DownloadOpportunitiesRequest(int method, String url, Map<String, String> headers, final Context context, final String location) {
        super(method, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        OppSearchResult result = SearchOpportunitiesExample.parseResult(response.toString());
                        if (!isResultOk(result)) {
                            return;
                        }
                        if (result.getCurrentPage() == 1 || result.getCurrentPage() == 0) {
                            VolunteerMatchApiService.enqueueOtherPages(result, context, location);
                        }
                        if (isEmulator()) {
                            for (Opportunities opp : result.getOpportunities()) {
                                opp.setDescription(opp.getDescription() + opp.getDescription() + opp.getDescription());
                            }
                        }
                        saveOpportunitiesAndGetData(result.getOpportunities(), context, location);
                        VolunteerApplication.getInstance().decrementRequestsRemaining();
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e(TAG, "Error: " + error.getMessage());
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).hideRecyclerViewLoadingIcon();
                        }
                    }
                });
        this.mHeaders = headers;
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    private static boolean isResultOk(OppSearchResult result) {
        if (result == null) {
            Log.e(TAG, "No internet connection, or couldn't communicate with server");
            return false;
        }
        if (result.getOpportunities() == null) {
            Log.d(TAG, "No opportunities in this area?");
            return false;
        }
        Log.d(TAG, "Finished searching for opportunities size: " + result.getOpportunities().size());
        Log.d(TAG, "Finished searching for currentPage: " + result.getCurrentPage());
        return true;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }
}
