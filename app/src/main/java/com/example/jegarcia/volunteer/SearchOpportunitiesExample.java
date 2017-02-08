package com.example.jegarcia.volunteer;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/**
 * Simple command line example of how one can call the VolunteerMatch public API to retrieve a list of opportunities.
 * This example makes 2 calls to the searchOpps method. The first will retrieve a full list of opportunities and the
 * second call shows how one would get a list of just the opportunities that have been updated since the first call was
 * made. This example is meant for someone who may want to maintain the list locally and only retrieve the updates on a
 * scheduled interval.
 *
 * Running the command with a "?" as a command line option will show the usage help.
 *
 * Created by jrackwitz on 12/3/15.
 */
public class SearchOpportunitiesExample {
    private static final String SEARCH_OPPORTUNITIES = "searchOpportunities";

    private static String url="http://www.stage.volunteermatch.org/api/call";
    private static String key = "0ed901afd6584a580e3aaf55484dec04";
    private static String user = "garciaj42";
    private static boolean debug = false;
    private static int displayCount = 3;
    private static String updatedSince = "2015-04-05T00:00:00Z";
    private Context mContext;

    public static final String TAG = VolunteerMatchApiService.class.getName();

    SearchOpportunitiesExample(Context context) {
        mContext = context;
        searchOpps(updatedSince, 10);
    }

    private void searchOpps(String updatedSince, int maxDisplay) {
        VolunteerMatchApiService service = new VolunteerMatchApiService();
//        service.setApiUrl(url); // this call is really only needed if you want use the stage server
        service.setContext(mContext);
        int pageNumber = 0;

        String searchOppsQuery = buildSearchOppsQuery(pageNumber, updatedSince);
        System.out.println("Search Query:");
        System.out.println(searchOppsQuery);
        service.execute(SEARCH_OPPORTUNITIES, searchOppsQuery, "GET", user, key);
    }

    static String buildSearchOppsQuery(int pageNumber, String updatedSince) {
        OppSearchQuery oq = new OppSearchQuery();
        oq.setLocation("san francisco");
        oq.setRadius("city");
        ArrayList<OppSearchQuery.DateRange> dateRanges = new ArrayList<>();
        OppSearchQuery.DateRange dr = new OppSearchQuery.DateRange();
        dr.setSingleDayOpps(true);
        dr.setStartDate("2017-02-01");
        dr.setEndDate("2017-02-06");
        if(updatedSince != null && updatedSince.length() > 0) {
            oq.setUpdatedSince(updatedSince);
            //oq.setIncludeInactive(true);
        }
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
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
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

    public void setContext(Context mContext) {
        this.mContext = mContext;
    }

    //    static private void printUsage() {
//        System.out.println("Valid command line arguments:" );
//        System.out.println("\nurl=http://www.stage.volunteermatch.org/api/call - This is the default and can be replaced with\n" +
//                "                                                   an alternate url of a test server." );
//        System.out.println("\nuser=APIUSer - API account name");
//        System.out.println("\nkey=APIKey - API account key");
//        System.out.println("\ndebug=false - Set to true to enable debug messages");
//        System.out.println("\ndisplayCount=3 - Number of opportunities to display (result set size and returned count is\n" +
//                "                 always displayed");
//        System.out.println("\nupdatedSince=2015-04-05T00:00:00Z - Only search for opportunities updated since this date.\n" +
//                "                                    Format must be: ISO 8601 standard form of\n" +
//                "                                    \"yyyy-MM-ddTHH:mm:ssZ\"");
//    }

//    /** Usage : java SearchOpportunitiesExample ? - will show usage
//     *
//     * @param args
//     */
//    public static void main(String[] args) {

//        System.out.println("Using URL: " + url);
//        System.out.println("Initial search.");
//        searchOpps(null, displayCount);

//        System.out.println("Search for just the updated opportunities.");
//        searchOpps(updatedSince, displayCount);
//    }


    //        OppSearchResult statusResult = null;
//        String result = null;
//        int displayCount = 1;
////        result = service.callAPI(SEARCH_OPPORTUNITIES, searchOppsQuery, "GET", user, key);
//        if (result == null) {
//            System.out.println("Error - failed to make API call");
//        }
//        System.out.println("Search Result:");
//        System.out.println(result);
//        if ((statusResult = parseResult(result)) != null) {
//            if (statusResult.getOpportunities().size() == 0) {
//                return Collections.emptyList();
//            }
//            int resultSize = statusResult.getResultsSize();
//                System.out.println("\nResult size: " + resultSize);
//                System.out.println("Number of result returned: " + statusResult.getOpportunities().size());
//                String displayMsg = maxDisplay < resultSize ? "Results (Limited output to first " + maxDisplay + " results):" : "Results: ";
//                System.out.println(displayMsg);
//                ArrayList<OppSearchResult.Opportunities> opps = statusResult.getOpportunities();
//                for (OppSearchResult.Opportunities opp : opps) {
//                    System.out.println("     Id: " + opp.getId());
//                    System.out.println("  Title: " + opp.getTitle());
//                    System.out.println("Updated: " + opp.getUpdated());
//                    System.out.println(" Active: " + opp.getStatus());
//                    System.out.println();
//                    if (displayCount++ == maxDisplay) {
//                        break;
//                    }
//                }
//            return statusResult.getOpportunities();
//        }
//        return Collections.emptyList();

}