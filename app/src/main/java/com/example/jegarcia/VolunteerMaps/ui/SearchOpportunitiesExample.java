package com.example.jegarcia.VolunteerMaps.ui;

import android.util.Log;

import com.example.jegarcia.VolunteerMaps.models.restModels.OppSearchQuery;
import com.example.jegarcia.VolunteerMaps.models.restModels.OppSearchResult;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.axis.utils.StringUtils;

import java.util.ArrayList;

import static com.example.jegarcia.VolunteerMaps.ui.VolunteerRequestUtils.formatDate;

/**
 * Simple command line example of how one can call the VolunteerMatch public API to retrieve a list of opportunities.
 * This example makes 2 calls to the searchOpportunities method. The first will retrieve a full list of opportunities and the
 * second call shows how one would get a list of just the opportunities that have been updated since the first call was
 * made. This example is meant for someone who may want to maintain the list locally and only retrieve the updates on a
 * scheduled interval.
 *
 * Running the command with a "?" as a command line option will show the usage help.
 *
 * Created by jrackwitz on 12/3/15.
 */
public class SearchOpportunitiesExample {
    public static final String SEARCH_OPPORTUNITIES = "searchOpportunities";

    private static String url="http://www.stage.volunteermatch.org/api/call";
//    private static String updatedSince = "2015-04-05T00:00:00Z";

    public static final String TAG = VolunteerMatchApiService.class.getName();

    private SearchOpportunitiesExample() {
    }

    public static String buildSearchOppsQuery(int pageNumber, String updatedSince, int daysSince, String location) {
        if (StringUtils.isEmpty(location)) {
            return null;
        }
        OppSearchQuery oq = new OppSearchQuery();
        oq.setLocation(location);
        oq.setRadius("city");//TODO maybe this could expand?
        ArrayList<OppSearchQuery.DateRange> dateRanges = new ArrayList<>();
        OppSearchQuery.DateRange dr = new OppSearchQuery.DateRange();
//        dr.setSingleDayOpps(true);
        dr.setStartDate(formatDate(0)); // Maybe always stay on today? defaults to today? check
        dr.setEndDate(formatDate(-daysSince));
        oq.setUpdatedSince("2015-04-05T00:00:00Z");
        dateRanges.add(dr);
        dr = new OppSearchQuery.DateRange();
        dr.setOngoing(true);
        dateRanges.add(dr);
        oq.setDateRanges(dateRanges);
        oq.setSortOrder("asc");
        oq.setSortCriteria("update");
        oq.setPageNumber(pageNumber);

        ArrayList<String> displayFields = new ArrayList<>();
        displayFields.add("id");
        displayFields.add("title");
        displayFields.add("updated");
        displayFields.add("status");
        displayFields.add("availability");
        displayFields.add("imageUrl");
        displayFields.add("contact");
        displayFields.add("volunteersNeeded");
        displayFields.add("skillsNeeded");
        displayFields.add("greatFor");
        displayFields.add("spacesAvailable");
        displayFields.add("keywords");
        oq.setFieldsToDisplay(displayFields);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(oq);
    }

    static OppSearchResult parseResult(String result) {
        OppSearchResult reportResult = null;
        if(result != null) {
            String resultArray[] = result.split("\n");
            try {
                Gson gson = new GsonBuilder()
                        .serializeNulls()
                        .disableHtmlEscaping()
                        .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                            @Override
                            public boolean shouldSkipField(FieldAttributes f) {
                                return f.getName().contains("downloadTime") || f.getName().contains("downloadedCity") || f.getName().contains("isLiked");
                            }

                            @Override
                            public boolean shouldSkipClass(Class<?> aClass) {
                                return false;
                            }
                        })
                        .create();
                reportResult = gson.fromJson(resultArray[0], OppSearchResult.class);
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
