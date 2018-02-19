package com.example.jegarcia.VolunteerMaps.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jegarcia.VolunteerMaps.R;
import com.example.jegarcia.VolunteerMaps.VolunteerApplication;
import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;
import com.example.jegarcia.VolunteerMaps.ui.instagram.InstagramPhotoAdapter;
import com.example.jegarcia.VolunteerMaps.ui.instagram.InstagramResponse;
import com.example.jegarcia.VolunteerMaps.ui.views.ExpandableTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.apache.axis.utils.StringUtils;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.view.View.GONE;
import static com.example.jegarcia.VolunteerMaps.ui.SearchOpportunitiesExample.SEARCH_OPPORTUNITIES;

public class OpportunityActivity extends Activity {

    @BindView(R.id.closeOpportunity)
    ImageButton closeOpportunityButton;

    @BindView(R.id.oppNameTitleBar)
    TextView oppNameTitleBar;

    @BindView(R.id.opportunityName)
    TextView opportunityName;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.timeTextView) //AKA availability
    TextView timeTextView;

    @BindView(R.id.description)
    ExpandableTextView description;

    @BindView(R.id.readMoreTextView)
    TextView readMore;

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

    @BindView(R.id.instagramList)
    RecyclerView instagramList;

    @BindView(R.id.instagramDescription)
    TextView instagramDescription;

    private int mOpportunityId;
    private boolean trim = true;

    private static final String TAG = OpportunityActivity.class.getSimpleName();
    public static final String CLIENT_ID = "bcbeab07a3fe4cabb3c3cac0a084e896";
    public static final String CLIENT_SECRET = "8efd5ec6ecae40a080264b2865210b3c";
    public static final String CALLBACK_URL = "redirect uri here";
    public static final String ACCESS_TOKEN = "6902294237.ba4c844.21071e2ccf9e4a14a305b650b02db40d";

//    https://api.instagram.com/v1/locations/{location-id}/media/recent?access_token=ACCESS-TOKEN

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
                            zip.setVisibility(GONE);
                        } else {
                            zip.setText(opportunity.getLocation().getPostalCode());
                        }

                        if (StringUtils.isEmpty(opportunity.getLocation().getAddress())) {
                            address.setVisibility(GONE);
                        } else {
                            address.setText(opportunity.getLocation().getAddress());
                        }

                        if (StringUtils.isEmpty(opportunity.getLocation().getCity())) {
                            location.setVisibility(GONE);
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
                        description.addEllipsizeListener(new ExpandableTextView.EllipsizeListener() {
                            @Override
                            public void ellipsizeStateChanged(boolean ellipsized) {
                                if (ellipsized) {
                                    readMore.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        readMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (description.isEllipsized()) {
                                    description.setMaxLines(1000);
                                    readMore.setText(R.string.less);
                                } else {
                                    description.setMaxLines(8);
                                    readMore.setText(R.string.more);
                                }
                            }
                        });

                        oppNameTitleBar.setText(String.valueOf(opportunity.getTitle()));
                        opportunityName.setText(String.valueOf(opportunity.getTitle()));

                        if (!StringUtils.isEmpty(opportunity.getImageUrl())) {
                            String decodedUrl = URLDecoder.decode(opportunity.getImageUrl());
                            Picasso.with(getBaseContext()).load(decodedUrl).into(imageView);
                        }

                        startVolleyRequest(opportunity.getParentOrg().getName(), ACCESS_TOKEN);

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

    public void startVolleyRequest(String tag,
                                   String accessToken) {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";
        String url = createInstagramUrl(tag, accessToken);
        createJsonObjectRequest(url);
    }

    private String createInstagramUrl(String tag, String accessToken) {
        StringBuilder sb = new StringBuilder();
        tag = tag.replace(" ", "");
        if (tag.contains("-")) {
            tag = tag.substring(0, tag.indexOf("-"));
        }
        tag = tag.replaceAll("[^a-zA-Z0-9]", "");
        String urlHead = "https://api.instagram.com/v1/tags/";
        String urlMiddle = "/media/recent?access_token=";
        sb.append(urlHead).append(tag).append(urlMiddle).append(accessToken);
        return sb.toString();
    }

    private void createJsonObjectRequest(String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        InstagramResponse instagramResponse = parseResult(response.toString());
                        VolunteerApplication.getInstance().decrementRequestsRemaining();
                        if (instagramResponse.getData() != null && instagramResponse.getData().length != 0) { //Empty Check
                            instagramDescription.setVisibility(View.VISIBLE);
                            instagramResponse.setData(Arrays.copyOfRange(instagramResponse.getData(), 0, instagramResponse.getData().length > 6 ? 6 : instagramResponse.getData().length));
                            instagramList.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                            InstagramPhotoAdapter instagramPhotoAdapter = new InstagramPhotoAdapter(getBaseContext(), instagramResponse.getData());
                            instagramList.setAdapter(instagramPhotoAdapter);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(TAG, "Error Calling Pinterest API: " + error.getMessage());
            }
        });

        // Adding request to request queue
        VolunteerApplication.getInstance().addToRequestQueue(jsonObjReq, "");
    }

    private InstagramResponse parseResult(String result) {
        InstagramResponse reportResult = null;
        if(result != null) {
            String resultArray[] = result.split("\n");
            try {
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .disableHtmlEscaping()
                        .create();
                reportResult = gson.fromJson(resultArray[0], InstagramResponse.class);
            } catch (Exception jbe) {
                System.out.println("Error decoding json result: " + jbe);
                Log.e(TAG, "Results:" + resultArray.toString());
                jbe.printStackTrace();
            }
        } else {
            System.out.println("Error calling " + SEARCH_OPPORTUNITIES + " API.");
        }
        return reportResult;
    }
}
