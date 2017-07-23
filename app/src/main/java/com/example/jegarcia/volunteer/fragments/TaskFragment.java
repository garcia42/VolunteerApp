package com.example.jegarcia.volunteer.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.jegarcia.volunteer.MainActivity;
import com.example.jegarcia.volunteer.VolunteerMatchApiService;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.SearchResultAdapter;

/**
 * This Fragment manages a single background task and retains
 * itself across configuration changes.
 */
public class TaskFragment extends Fragment {

    private String httpMethod;
    private String searchQuery;
    private String restMethod;

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskCallbacks {
        void onPreExecute();

        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute();
    }

    private TaskCallbacks mCallbacks;
    private VolunteerMatchApiService mTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     * service.execute(SEARCH_OPPORTUNITIES, searchOppsQuery, VolunteerMatchApiService.HTTP_METHOD_GET);
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        start();
    }

    public void updateData(int recyclerView,
                           String httpMethod,
                           String searchQuery,
                           String restMethod,
                           boolean start) {

        this.httpMethod = httpMethod;
        this.searchQuery = searchQuery;
        this.restMethod = restMethod;
        if (start) {
            start();
        }
    }

    public void start() {
//        String httpMethod = getArguments().getString("httpMethod", "GET");
//        String searchQuery = getArguments().getString("query", "");
//        String restMethod = getArguments().getString("restMethod", "searchOpportunities");

        MainActivity mainActivity = (MainActivity) getContext();
        SearchResultAdapter adapter = mainActivity.getAdapter();

        mTask = new VolunteerMatchApiService(getContext());
        mTask.execute(restMethod, searchQuery, httpMethod);

    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

}