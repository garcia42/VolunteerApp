package com.example.jegarcia.volunteer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jegarcia.volunteer.R;
import com.example.jegarcia.volunteer.SearchOpportunitiesExample;
import com.example.jegarcia.volunteer.VolunteerMatchApiService;
import com.example.jegarcia.volunteer.models.Opportunities;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.EndlessRecyclerViewScrollListener;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.SearchResultAdapter;

import java.util.ArrayList;

import static com.example.jegarcia.volunteer.VolunteerRequestUtils.daysSince;
import static com.example.jegarcia.volunteer.VolunteerRequestUtils.formatDateAndTime;

public class VolunteerList extends Fragment {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SearchResultAdapter mAdapter;
    private static final String TAG_TASK_FRAGMENT = "tag_task_fragment";
    private TaskFragment mTaskFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View volunteerListPage = inflater.inflate(R.layout.content_main, container, false);
        setUpRecyclerView(volunteerListPage);
        return volunteerListPage;
    }

    private void setUpRecyclerView(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.opportunitiesView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SearchResultAdapter(new ArrayList<Opportunities>(), getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                searchOpportunities(daysSince, page);
            }
        });
        searchOpportunities(daysSince, 0);

        Button mapButton = (Button) v.findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, new Map());
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void searchOpportunities(int daysSince, int pageNumber) {

//        service.setApiUrl("http://www.stage.volunteermatch.org/api/call"); // this call is really only needed if you want use the stage server
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            String updatedSince = formatDateAndTime(daysSince);
            String searchOppsQuery = SearchOpportunitiesExample.buildSearchOppsQuery(pageNumber, updatedSince, daysSince);
            mTaskFragment = TaskFragment.newInstance(0, VolunteerMatchApiService.HTTP_METHOD_GET, searchOppsQuery, SearchOpportunitiesExample.SEARCH_OPPORTUNITIES);
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
    }

    public SearchResultAdapter getAdapter() {
        return mAdapter;
    }

}
