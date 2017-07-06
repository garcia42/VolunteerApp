package com.example.jegarcia.volunteer;

import com.example.jegarcia.volunteer.models.volunteerMatchModels.Opportunities;

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

    private VolunteerRequestUtils() {
        //Private Constructor so it can't be created
    }

    public static String formatDate(int daysSince) {
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
        if (opportunity.getAvailability() != null
                && !StringUtils.isEmpty(opportunity.getAvailability().getEndDate())) {
            String endDateString = opportunity.getAvailability().getEndDate();
            String lastUpdatedString = opportunity.getUpdated();
//            if (!StringUtils.isEmpty(opportunity.getAvailability().getEndTime())) { //End time could be null //TODO add time to expire
//                endDateString += " " + opportunity.getAvailability().getEndTime();
//            }
            DateTime endDate = jodaDateFormat.parseDateTime(endDateString);
            DateTime lastUpdated = !StringUtils.isEmpty(lastUpdatedString) ? jodaDateFormat.parseDateTime(lastUpdatedString) : new DateTime();

            if (endDate.isBefore(new DateTime()) || lastUpdated.isBefore(new DateTime().minusMonths(2))) {
                return true;
            }
        }
        return false;
    }
}
