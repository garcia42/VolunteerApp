package com.example.jegarcia.VolunteerMaps.ui.apiCall;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import static com.example.jegarcia.VolunteerMaps.ui.apiCall.VolunteerRequestUtils.daysSince;

public class DownloadOpportunitiesService extends IntentService {

    private static final String TAG = DownloadOpportunitiesService.class.getSimpleName();

    public DownloadOpportunitiesService() {
        super("DisplayNotification");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadOpportunitiesService(String name) {
        super(name);
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String location = intent.getStringExtra("location");
            VolunteerMatchApiService.downloadAllOppsInArea(0, daysSince, this, location);
        }
    }
}
