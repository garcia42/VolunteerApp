package com.example.jegarcia.volunteer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class VolunteerRequestUtils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
}
