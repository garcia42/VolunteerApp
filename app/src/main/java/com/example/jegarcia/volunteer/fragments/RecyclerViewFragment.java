package com.example.jegarcia.volunteer.fragments;
/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jegarcia.volunteer.MainActivity;
import com.example.jegarcia.volunteer.R;
import com.example.jegarcia.volunteer.SearchOpportunitiesExample;
import com.example.jegarcia.volunteer.VolunteerMatchApiService;
import com.example.jegarcia.volunteer.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.EndlessRecyclerViewScrollListener;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.RecyclerViewClickListener;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.SearchResultAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.jegarcia.volunteer.VolunteerRequestUtils.daysSince;
import static com.example.jegarcia.volunteer.VolunteerRequestUtils.formatDateAndTime;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class RecyclerViewFragment extends Fragment implements RecyclerViewClickListener {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final String TAG_TASK_FRAGMENT = "tag_task_fragment";
    private static final String KEY_LAYOUT_STATE = "layoutManagerState";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected SearchResultAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    private TaskFragment mTaskFragment;
    private Realm realm;

    @Override
    public void recyclerViewListClicked(View v, int position) {
        if (mAdapter != null) {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            OpportunityFragment organizationFragment = new OpportunityFragment();
            Bundle b = new Bundle();
            Opportunities opportunities = mAdapter.getItem(position);
            if (opportunities != null) {
                b.putInt("opportunity_id", opportunities.getOppId());
            } else {
                Log.e(TAG, "Opportunity null when showing oppFragment");
            }
            organizationFragment.setArguments(b);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, organizationFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invokeTaskFragment(0, daysSince);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.opportunitiesView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

//        ((MainActivity) getActivity()).resetRealm();
        realm = ((MainActivity) getActivity()).getRealm();

        RealmResults<Opportunities> opportunitiesRealmResults = realm
                .where(Opportunities.class).findAllAsync();
//                .findAllSorted("id", Sort.ASCENDING);

        mAdapter = new SearchResultAdapter(opportunitiesRealmResults, getActivity(), this);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                invokeTaskFragment(page + 1, daysSince);
            }
        });
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        setupMap(rootView);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        savedInstanceState.putParcelable(KEY_LAYOUT_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private void invokeTaskFragment(int pageNumber, int daysSince) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        String updatedSince = formatDateAndTime(daysSince);
        String searchOppsQuery = SearchOpportunitiesExample.buildSearchOppsQuery(pageNumber, updatedSince, daysSince);
        boolean start = true;
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            start = false;
        }
        mTaskFragment.updateData(0, VolunteerMatchApiService.HTTP_METHOD_GET, searchOppsQuery, SearchOpportunitiesExample.SEARCH_OPPORTUNITIES, start);
    }

    private void setupMap(View v) {
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

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    public SearchResultAdapter getAdapter() {
        return mAdapter;
    }
}