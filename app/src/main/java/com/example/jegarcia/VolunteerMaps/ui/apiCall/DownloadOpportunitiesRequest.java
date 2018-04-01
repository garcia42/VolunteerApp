package com.example.jegarcia.VolunteerMaps.ui.apiCall;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.models.restModels.OppSearchResult;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerMatchApiService.saveOpportunitiesAndGetData;

public class DownloadOpportunitiesRequest extends JsonObjectRequest {

    private static final String TAG = DownloadOpportunitiesRequest.class.getSimpleName();
    private static final String PREFS_NAME = "volunteerPrefsConfig";

    private Map<String, String> mHeaders;
    private String mLocation;
    private Context mContext;
    private final Gson gson;

    public DownloadOpportunitiesRequest(int method, String url, Map<String, String> headers, final Context context, final String location, Gson gson) {
        super(method, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        this.mHeaders = headers;
        this.mLocation = location;
        this.mContext = context;
        this.gson = gson;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String json = null;
        try {
            json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        OppSearchResult result = gson.fromJson(json, OppSearchResult.class);

        if (result.getCurrentPage() == result.getResultsSize() / 20) { //TODO math might be off
            SharedPreferences.Editor editor = mContext.getSharedPreferences(PREFS_NAME, 0).edit();
            String key = mContext.getString(R.string.last_check_date) + mLocation;
            editor.putString(key, VolunteerRequestUtils.formatDateAndTime(0)).apply();
        }

        if (!isResultOk(result)) {
            return Response.error(new VolleyError(response));
        }
        if (result.getCurrentPage() == 1 || result.getCurrentPage() == 0) {
            VolunteerMatchApiService.enqueueOtherPages(result, mContext, mLocation);
        }
        if (isEmulator()) {
            for (Opportunities opp : result.getOpportunities()) {
                opp.setDescription(opp.getDescription() + opp.getDescription() + opp.getDescription());
            }
        }
        saveOpportunitiesAndGetData(result.getOpportunities(), mContext, mLocation);
        return super.parseNetworkResponse(response);
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
