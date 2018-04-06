package com.example.jegarcia.VolunteerMaps.ui.activity;

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

import static android.view.View.GONE;
import static com.example.jegarcia.VolunteerMaps.ui.apiCall.SearchOpportunitiesExample.SEARCH_OPPORTUNITIES;

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

    @BindView(R.id.expand_text_view)
    com.ms.square.android.expandabletextview.ExpandableTextView description;

    @BindView(R.id.expandable_text)
    TextView descriptionInnerText;

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

    private static final String TAG = OpportunityActivity.class.getSimpleName() + "Jesus";
    public static final String CLIENT_ID = "bcbeab07a3fe4cabb3c3cac0a084e896";
    public static final String CLIENT_SECRET = "8efd5ec6ecae40a080264b2865210b3c";
    public static final String CALLBACK_URL = "redirect uri here";
    public static final String ACCESS_TOKEN = "6902294237.ba4c844.21071e2ccf9e4a14a305b650b02db40d";

    private int mOpportunityId;
    private String mImageUrl;
    private String mParentOrgName;
    private String mStartDate;
    private String mEndDate;
    private String mPostalCode;
    private String mAddress;
    private String mCity;
    private String mDescription;
    private boolean mIsLiked;
    private String mTitle;

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
        try (Realm realm = Realm.getDefaultInstance()) {
            // Get a Realm instance for this thread

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Opportunities opportunity = realm.where(Opportunities.class).equalTo("id", mOpportunityId).findFirst();
                    if (opportunity != null) {
                        mImageUrl = opportunity.getImageUrl();
                        mParentOrgName = opportunity.getParentOrg().getName();
                        mStartDate = opportunity.getAvailability().getStartDate();
                        mEndDate = opportunity.getAvailability().getEndDate();
                        mPostalCode = opportunity.getLocation().getPostalCode();
                        mAddress = opportunity.getLocation().getAddress();
                        mCity = opportunity.getLocation().getAddress();
                        mDescription = opportunity.getDescription();
                        mIsLiked = opportunity.isLiked();
                        mTitle = opportunity.getTitle();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    if (!StringUtils.isEmpty(mImageUrl)) {
                        String decodedUrl = URLDecoder.decode(mImageUrl);
                        Picasso.with(getBaseContext()).load(decodedUrl).into(imageView);
                    }
                    startVolleyRequest(mParentOrgName, ACCESS_TOKEN);

                    if (StringUtils.isEmpty(mStartDate)) {
                        timeTextView.setText(R.string.ongoing_efforts);
                    } else {
                        try {
                            StringBuilder dateText = new StringBuilder();
                            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(mStartDate);
                            dateText.append(new SimpleDateFormat("EEEE, MMMM d yyyy").format(date));
                            if (!StringUtils.isEmpty(mStartDate) && !mStartDate.equals(mEndDate)) {
                                date = new SimpleDateFormat("yyyy-MM-dd").parse(mEndDate);
                                dateText.append(" - ");
                                dateText.append(new SimpleDateFormat("EEEE, MMMM d yyyy").format(date));
                            }
                            timeTextView.setText(dateText.toString());
                        } catch (ParseException e) {
                            timeTextView.setText(mStartDate);
                            e.printStackTrace();
                        }
                    }

                    if (StringUtils.isEmpty(mPostalCode)) {
                        zip.setVisibility(GONE);
                    } else {
                        zip.setText(mPostalCode);
                    }

                    if (StringUtils.isEmpty(mAddress)) {
                        address.setVisibility(GONE);
                    } else {
                        address.setText(mAddress);
                    }

                    if (StringUtils.isEmpty(mCity)) {
                        location.setVisibility(GONE);
                    } else {
                        location.setText(mCity);
                    }
                    parentOrg.setText(String.valueOf(mParentOrgName));

                    if (Build.VERSION.SDK_INT >= 24) {
                        description.setText(Html.fromHtml(mDescription, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        description.setText(Html.fromHtml(mDescription));
                    }
                    Linkify.addLinks(descriptionInnerText, Linkify.ALL);

                    descriptionInnerText.setMovementMethod(LinkMovementMethod.getInstance());
                    oppNameTitleBar.setText(String.valueOf(mTitle));
                    opportunityName.setText(String.valueOf(mTitle));

                    sparkButton.setChecked(mIsLiked);
                    sparkButton.setEventListener(new SparkEventListener() {
                        @Override
                        public void onEvent(ImageView imageView, final boolean b) {
                            try (Realm realm = Realm.getDefaultInstance()) {
                                realm.executeTransactionAsync(new Realm.Transaction() {

                                    @Override
                                    public void execute(Realm realm) {
                                        Opportunities modifyOpp = realm.where(Opportunities.class).equalTo("id", mOpportunityId).findFirst();
                                        if (modifyOpp != null) {
                                            modifyOpp.setLiked(b);
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
            });
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
                VolleyLog.e(TAG, "Error Calling Instagram API: " + error.getMessage());
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
                Log.e(TAG, "Results:" + Arrays.toString(resultArray));
                jbe.printStackTrace();
            }
        } else {
            System.out.println("Error calling " + SEARCH_OPPORTUNITIES + " API.");
        }
        return reportResult;
    }
}
