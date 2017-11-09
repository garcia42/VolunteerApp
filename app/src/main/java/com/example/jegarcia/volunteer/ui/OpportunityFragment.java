package com.example.jegarcia.volunteer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OpportunityFragment extends Fragment {

//    @BindView(R.id.titleView)
//    TextView titleView;
//
//    @BindView(R.id.updatedView)
//    TextView updatedView;
//
//    @BindView(R.id.statusView)
//    TextView statusView;
//
//    @BindView(R.id.imageView)
//    ImageView imageView;
//
//    @BindView(R.id.volunteersNeeded)
//    TextView volunteersNeeded;
//
//    @BindView(R.id.contact)
//    TextView contact;
//
//    @BindView(R.id.availability)
//    TextView availability;
//
//    @BindView(R.id.skillsNeeded)
//    TextView skillsNeeded;
//
//    @BindView(R.id.greatFor)
//    TextView greatFor;
//
//    @BindView(R.id.descriptions)
//    TextView descriptions;
//
//    @BindView(R.id.minimumAge)
//    TextView minimumAge;
//
//    @BindView(R.id.numReferred)
//    TextView numReferred;
//
//    @BindView(R.id.spacesAvailable)
//    TextView spacesAvailable;
//
//    @BindView(R.id.parentOrg)
//    TextView parentOrg;
//
//    @BindView(R.id.location)
//    TextView location;
//
//    @BindView(R.id.requiresAddress)
//    TextView requiresAddress;
//
//    @BindView(R.id.virtual)
//    TextView virtual;

    private int mOpportunityId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.opportunity_page, container, false);
//        ButterKnife.bind(this, rootView);
//        mOpportunityId = getArguments().getInt("opportunity_id", -1);
//        setupOpportunityViews();
//        return rootView;
        return null;
    }

    private void setupOpportunityViews() {

//        Realm realm = null;
//        try {
//            // Get a Realm instance for this thread
//            realm = Realm.getDefaultInstance();
//
//            realm.executeTransaction(new Realm.Transaction() {
//
//                @Override
//                public void execute(Realm realm) {
//                    final RealmResults<Opportunities> opportunities = realm.where(Opportunities.class).equalTo("id", mOpportunityId).findAll();
//                    for (Opportunities opportunity : opportunities) { // YES
//                        virtual.setText(String.valueOf(opportunity.isVirtual()));
//                        requiresAddress.setText(String.valueOf(opportunity.isRequiresAddress()));
//                        location.setText(String.valueOf(opportunity.getLocation()));
//                        parentOrg.setText(String.valueOf(opportunity.getParentOrg()));
//                        spacesAvailable.setText(String.valueOf(opportunity.getSpacesAvailable()));
//                        numReferred.setText(String.valueOf(opportunity.getNumReferred()));
//                        minimumAge.setText(String.valueOf(opportunity.getMinimumAge()));
//                        descriptions.setText(String.valueOf(opportunity.getDescriptions()));
//                        greatFor.setText(String.valueOf(opportunity.getGreatFor()));
//                        skillsNeeded.setText(String.valueOf(opportunity.getSkillsNeeded()));
//                        availability.setText(String.valueOf(opportunity.getAvailability()));
//                        contact.setText(String.valueOf(opportunity.getContact()));
//                        volunteersNeeded.setText(String.valueOf(opportunity.getVolunteersNeeded()));
//                        statusView.setText(String.valueOf(opportunity.getStatus()));
//                        updatedView.setText(String.valueOf(opportunity.getUpdated()));
//                        titleView.setText(String.valueOf(opportunity.getTitle()));
//                        statusView.setText(String.valueOf(opportunity.getStatus()));
//
//                        if (!StringUtils.isEmpty(opportunity.getImageUrl())) {
//                            String decodedUrl = URLDecoder.decode(opportunity.getImageUrl());
//                            Picasso.with(getContext()).load(decodedUrl).into(imageView);
//                        }
//                    }
//                }
//            });
//        } finally {
//            if (realm != null) {
//                realm.close();
//            }
//        }
    }
}
