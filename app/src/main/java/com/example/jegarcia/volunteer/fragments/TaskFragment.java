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

    public static TaskFragment newInstance(int recyclerView,
                                           String httpMethod,
                                           String searchQuery,
                                           String restMethod) {
        TaskFragment myFragment = new TaskFragment();

        Bundle args = new Bundle();
        args.putInt("recyclerView_index", recyclerView);
        args.putString("httpMethod", httpMethod);
        args.putString("query", searchQuery);
        args.putString("restMethod", restMethod);
        myFragment.setArguments(args);

        return myFragment;
    }

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

//        int index = getArguments().getInt("recyclerView_index", 0);
        String httpMethod = getArguments().getString("httpMethod", "GET");
        String query = getArguments().getString("query", "");
        String restMethod = getArguments().getString("restMethod", "searchOpportunities");

        MainActivity mainActivity = (MainActivity) getContext();
        SearchResultAdapter adapter = mainActivity.getAdapter();

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        // Create and execute the background task.
        mTask = new VolunteerMatchApiService(adapter);
        mTask.execute(restMethod, query, httpMethod);
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