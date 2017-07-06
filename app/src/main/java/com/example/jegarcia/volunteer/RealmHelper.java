package com.example.jegarcia.volunteer;

import com.example.jegarcia.volunteer.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.volunteer.volunteerMatchRecyclerView.SearchResultAdapter;

import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    public static void storeOpportunities(final List<Opportunities> opportunities) {
        Realm realm = null;
        if (opportunities.size() > 0) {
            try {
                // Get a Realm instance for this thread
                realm = Realm.getDefaultInstance();

                realm.executeTransaction(new Realm.Transaction() {

                    @Override
                    public void execute(Realm realm) {
                        for (Opportunities opportunity : opportunities) {
                            Opportunities user = realm.where(Opportunities.class).equalTo("id", opportunity.getOppId()).findFirst();
                            if (StringUtils.isEmpty(opportunity.getUpdated()) && user == null) {
                                opportunity.setUpdated(VolunteerRequestUtils.formatDate(0)); //Today
                            }
                            realm.copyToRealmOrUpdate(opportunity);
                        }
                    }
                });
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }
    }

    public static void getOpportunities(final SearchResultAdapter searchResultAdapter) {
        Realm realm = null;
        try {
            // Get a Realm instance for this thread
            realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    RealmResults<Opportunities> opportunities = realm.where(Opportunities.class).findAll();
                    List<Opportunities> notExpired = new ArrayList<>();
//                    for (Opportunities opportunity: opportunities) {
//                        if (VolunteerRequestUtils.isExpiredOpportunity(opportunity)) {
//                            opportunity.deleteFromRealm();
//                        } else {
//                            notExpired.add(opportunity);
//                        }
//                    }
                    searchResultAdapter.addItems(notExpired);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
