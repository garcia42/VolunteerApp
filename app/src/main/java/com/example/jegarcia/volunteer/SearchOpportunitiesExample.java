package com.example.jegarcia.volunteer;

import com.example.jegarcia.volunteer.models.OppSearchQuery;
import com.example.jegarcia.volunteer.models.OppSearchResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import static com.example.jegarcia.volunteer.VolunteerRequestUtils.formatDate;

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
    private static int displayCount = 3;
//    private static String updatedSince = "2015-04-05T00:00:00Z";

    public static final String TAG = VolunteerMatchApiService.class.getName();

    private SearchOpportunitiesExample() {
    }

    public static String buildSearchOppsQuery(int pageNumber, String updatedSince, int daysSince) {
        OppSearchQuery oq = new OppSearchQuery();
        oq.setLocation("san francisco"); //TODO should be dynamic
        oq.setRadius("city");//TODO maybe this could expand?
        ArrayList<OppSearchQuery.DateRange> dateRanges = new ArrayList<>();
        OppSearchQuery.DateRange dr = new OppSearchQuery.DateRange();
//        dr.setSingleDayOpps(true);
        dr.setStartDate(formatDate(0)); // Maybe always stay on today? defaults to today? check
        dr.setEndDate(formatDate(-daysSince));
        if(updatedSince != null && updatedSince.length() > 0) {
            oq.setUpdatedSince("2015-04-05T00:00:00Z");
            //oq.setIncludeInactive(true);
        }
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
        oq.setFieldsToDisplay(displayFields);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(oq);
    }

    static OppSearchResult parseResult(String result) {
        OppSearchResult reportResult = null;
        if(result != null) {
            String resultArray[] = result.split("\n");
            if (resultArray.length == 2) {
                try {
                    Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
                    reportResult = gson.fromJson(resultArray[1], OppSearchResult.class);
                } catch (Exception jbe) {
                    System.out.println("Error decoding json result: " + jbe);
                }
            } else if (resultArray.length == 1) {
                System.out.println("Error calling " + SEARCH_OPPORTUNITIES + " API. Returned: " + resultArray[0]);
            }
        } else {
            System.out.println("Error calling " + SEARCH_OPPORTUNITIES + " API.");
        }
        return reportResult;
    }
}
