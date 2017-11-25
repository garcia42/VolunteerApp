package com.example.jegarcia.VolunteerMaps.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.apache.axis.utils.StringUtils;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class OpportunityActivity extends Activity {

    @BindView(R.id.closeOpportunity)
    ImageButton closeOpportunityButton;

    @BindView(R.id.opportunityName)
    TextView opportunityName;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.timeTextView) //AKA availability
    TextView timeTextView;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.hostTextView)
    TextView parentOrg;

    @BindView(R.id.placeTextView)
    TextView location;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.zipCode)
    TextView zip;

    @BindView(R.id.star_button)
    SparkButton sparkButton;

    private int mOpportunityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opportunity);
        ButterKnife.bind(this);
        mOpportunityId = getIntent().getExtras().getInt("opportunity_id", -1);
        setupOpportunityViews();
    }

    @OnClick(R.id.closeOpportunity)
    public void closeOpportunity(View view) {
        finish();
    }

    private void setupOpportunityViews() {
        Realm realm = null;
        try {
            // Get a Realm instance for this thread
            realm = Realm.getDefaultInstance();

            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    final RealmResults<Opportunities> opportunities = realm.where(Opportunities.class).equalTo("id", mOpportunityId).findAll();
                    for (Opportunities opportunity : opportunities) { // YES
                        final int oppId = opportunity.getOppId();
                        if (StringUtils.isEmpty(opportunity.getAvailability().getStartDate())) {
                            timeTextView.setText("Ongoing Volunteering Efforts");
                        } else {

                            try {
                                StringBuilder dateText = new StringBuilder();
                                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(opportunity.getAvailability().getStartDate());
                                dateText.append(new SimpleDateFormat("EEEE, MMMM d yyyy").format(date));
                                if (!StringUtils.isEmpty(opportunity.getAvailability().getStartDate()) &&
                                        !opportunity.getAvailability().getStartDate().equals(opportunity.getAvailability().getEndDate())) {
                                    date = new SimpleDateFormat("yyyy-MM-dd").parse(opportunity.getAvailability().getEndDate());
                                    dateText.append(" - ");
                                    dateText.append(new SimpleDateFormat("EEEE, MMMM d yyyy").format(date));
                                }
                                timeTextView.setText(dateText.toString());
                            } catch (ParseException e) {
                                timeTextView.setText(opportunity.getAvailability().getStartDate());
                                e.printStackTrace();
                            }

                            opportunity.getAvailability().getStartDate();
                            opportunity.getAvailability().getEndDate();
                            String.valueOf(opportunity.getAvailability());
                        }

                        if (StringUtils.isEmpty(opportunity.getLocation().getPostalCode())) {
                            zip.setVisibility(View.GONE);
                        } else {
                            zip.setText(opportunity.getLocation().getPostalCode());
                        }

                        if (StringUtils.isEmpty(opportunity.getLocation().getAddress())) {
                            address.setVisibility(View.GONE);
                        } else {
                            address.setText(opportunity.getLocation().getAddress());
                        }

                        if (StringUtils.isEmpty(opportunity.getLocation().getCity())) {
                            location.setVisibility(View.GONE);
                        } else {
                            location.setText(opportunity.getLocation().getCity());
                        }
                        parentOrg.setText(String.valueOf(opportunity.getParentOrg().getName()));

                        if (Build.VERSION.SDK_INT >= 24)
                        {
                            description.setText(Html.fromHtml(opportunity.getDescription() , Html.FROM_HTML_MODE_LEGACY));
                        }
                        else
                        {
                            description.setText(Html.fromHtml(opportunity.getDescription()));
                        }
                        Linkify.addLinks(description, Linkify.ALL);
                        description.setMovementMethod(LinkMovementMethod.getInstance());
                        opportunityName.setText(String.valueOf(opportunity.getTitle()));

                        if (!StringUtils.isEmpty(opportunity.getImageUrl())) {
                            String decodedUrl = URLDecoder.decode(opportunity.getImageUrl());
                            Picasso.with(getBaseContext()).load(decodedUrl).into(imageView);
                        }

                        sparkButton.setChecked(opportunity.isLiked());
                        sparkButton.setEventListener(new SparkEventListener(){
                            @Override
                            public void onEvent(ImageView imageView, boolean b) {
                                Realm realm = Realm.getDefaultInstance();
                                if (b) {
                                    realm.executeTransactionAsync(new Realm.Transaction() {

                                        @Override
                                        public void execute(Realm realm) {
                                            Opportunities modifyOpp = realm.where(Opportunities.class).equalTo("id", oppId).findFirst();
                                            if (modifyOpp != null) {
                                                modifyOpp.setLiked(true);
                                            }
                                        }
                                    });
                                } else {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            Opportunities modifyOpp = realm.where(Opportunities.class).equalTo("id", oppId).findFirst();
                                            if (modifyOpp != null) {
                                                modifyOpp.setLiked(false);
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onEventAnimationEnd(ImageView imageView, boolean b) {

                            }

                            @Override
                            public void onEventAnimationStart(ImageView imageView, boolean b) {

                            }
                        });
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
