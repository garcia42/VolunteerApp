package com.example.jegarcia.VolunteerMaps.ui.apiCall;

import com.example.jegarcia.VolunteerMaps.models.volunteerMatchModels.Opportunities;

import org.apache.axis.utils.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class VolunteerRequestUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateTimeFormatter jodaDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final int daysSince = 15;

    public static final String LOCATION = "LOCATION";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String IS_SAVED = "IS_SAVED";

    private VolunteerRequestUtils() {
        //Private Constructor so it can't be created
    }

    static String formatDate(int daysSince) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -daysSince);
        return dateFormat.format(calendar.getTime());
    }

    public static String formatDateAndTime(int daysSince) {
        Date updatedSince = null;
        try {
            updatedSince = dateFormat.parse(formatDate(daysSince));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateAndTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateAndTimeFormat.format(updatedSince);
    }

    public static boolean isExpiredOpportunity(Opportunities opportunity) {
        if (opportunity.getAvailability() != null) {
            String endDateString = opportunity.getAvailability().getEndDate();
            String startDateString = opportunity.getAvailability().getStartDate();
            String lastUpdatedString = opportunity.getUpdated();

            if (!StringUtils.isEmpty(endDateString)) { //End Date expired
                DateTime endDate = jodaDateFormat.parseDateTime(endDateString);
                if (endDate.isBefore(new DateTime())) {
                    return true;
                }
            } else if (!StringUtils.isEmpty(startDateString)) { //If end date is empty but start date isn't, if start date is expired then remove
                DateTime startDate = jodaDateFormat.parseDateTime(startDateString);
                if (startDate.isBefore(new DateTime())) {
                    return true;
                }
            }

            DateTime lastUpdated = !StringUtils.isEmpty(lastUpdatedString) ? jodaDateFormat.parseDateTime(lastUpdatedString) : new DateTime();

            if (lastUpdated.isBefore(new DateTime().minusWeeks(1))) { // Last updated is one week old
                return true;
            }
        }
        return false;
    }
}
