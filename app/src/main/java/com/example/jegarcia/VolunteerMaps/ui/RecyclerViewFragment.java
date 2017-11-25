package com.example.jegarcia.VolunteerMaps.ui;
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

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.VolunteerMaps.ui.volunteerMatchRecyclerView.EndlessRecyclerViewScrollListener;
import com.example.jegarcia.VolunteerMaps.ui.volunteerMatchRecyclerView.RecyclerViewClickListener;
import com.example.jegarcia.VolunteerMaps.ui.volunteerMatchRecyclerView.SearchResultAdapter;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.IS_SAVED;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.LATITUDE;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.LOCATION;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.LONGITUDE;
import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.daysSince;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class RecyclerViewFragment extends Fragment implements RecyclerViewClickListener {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private String location;
    private double longitude;
    private double latitude;
    private boolean isSaved;

//    @BindView(R.id.loadingLayout)
    LinearLayout loadingLayout;
    TextView emptyListTextView;

    public void stopLoadingIcon() {
        if (loadingLayout == null && getActivity() != null) {
            loadingLayout = (LinearLayout) getActivity().findViewById(R.id.loadingLayout);
        }
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }
        if (mScrollListener != null) {
            mScrollListener.setLoadingFalse();
        }
    }

    public void showLoadingIcon() {
        if (loadingLayout == null && getActivity() != null) {
            loadingLayout = (LinearLayout) getActivity().findViewById(R.id.loadingLayout);
        }
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
        }
    }

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected SearchResultAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    private EndlessRecyclerViewScrollListener mScrollListener;

    @Override
    public void recyclerViewListClicked(View v, int position) {
        if (mAdapter != null) {
            Intent intent = new Intent(getActivity(), OpportunityActivity.class);

            Opportunities opportunities = mAdapter.getItem(position);
            if (opportunities != null) {
                intent.putExtra("opportunity_id", opportunities.getOppId());
            } else {
                Log.e(TAG, "Opportunity null when showing oppFragment");
            }
            startActivity(intent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            location = bundle.getString(LOCATION, "");
            latitude = bundle.getDouble(LATITUDE, 0);
            longitude = bundle.getDouble(LONGITUDE, 0);
            isSaved = bundle.getBoolean(IS_SAVED, false);
        }
        MainActivity activity = (MainActivity) getActivity();
        if (!RealmHelper.hasOpportunitiesNearby(latitude, longitude)) { //Only do this on initialization
            Log.d(TAG, "No Opportunities, download page 0");
            activity.invokeTaskFragment(0, daysSince, getContext(), location);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);
//        ButterKnife.bind(this, rootView);
        rootView.setTag(TAG);
        Log.d(TAG, "RecyclerViewFragment onCreateView");

        loadingLayout = (LinearLayout) rootView.findViewById(R.id.loadingLayout);
        if (!RealmHelper.hasOpportunitiesNearby(latitude, longitude)) {
            if (!isSaved) {
                Log.d(TAG, "Show Loading Layout");
                loadingLayout.setVisibility(View.VISIBLE);
            }
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.opportunitiesView);
        emptyListTextView = (TextView) rootView.findViewById(R.id.emptyListTextView);
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

        Realm realm = ((MainActivity) getActivity()).getRealm();
//        RealmResults<Opportunities> opportunitiesRealmResults = realm
//                .where(Opportunities.class).contains("location.downloadedCity", location, Case.INSENSITIVE).findAllAsync();
        double box = RealmHelper.getBoundingLimits();
        RealmResults<Opportunities> opportunitiesRealmResults = null;
        if (!isSaved) {
            opportunitiesRealmResults = realm
                    .where(Opportunities.class)
                    .greaterThan("location.geoLocation.latitude", latitude - box)
                    .lessThan("location.geoLocation.latitude", latitude + box)
                    .greaterThan("location.geoLocation.longitude", longitude - box)
                    .lessThan("location.geoLocation.longitude", longitude + box)
                    .findAllAsync();
            mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    Log.d(TAG, "Loading More: Page " + page + " Total Count: " + totalItemsCount);
                    ((MainActivity) getActivity()).invokeTaskFragment(page, daysSince, getContext(), location);
                }
            };
            mRecyclerView.addOnScrollListener(mScrollListener);
        } else {
            opportunitiesRealmResults = realm
                    .where(Opportunities.class)
                    .equalTo("isLiked", true)
                    .findAllAsync();
        }
        emptyListTextView.setText(isSaved ? "No Saved Opportunities Yet" : "Finding Volunteer Opportunities!");
        mAdapter = new SearchResultAdapter(opportunitiesRealmResults, getActivity(), this, isSaved);
        mAdapter.setHasStableIds(true);
        mAdapter.setEmptyView(emptyListTextView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
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