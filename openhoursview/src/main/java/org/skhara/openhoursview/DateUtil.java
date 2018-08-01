package org.skhara.openhoursview;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;
    static final int MINUTES_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR;

    /**
     * Return the current day's index in the Svod's OpenHours array
     */
    public static int getDayOfWeekIndex(Date aDate) {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(aDate);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) { // set sunday to end
            dayOfWeek = 8;
        }
        dayOfWeek = dayOfWeek - 2;
        return dayOfWeek;
    }

    public static int getPreviousIndex(int aCurrentIndex) {
        return aCurrentIndex > 0 ? aCurrentIndex - 1 : 6;
    }

    public static int inMinutes(int hours, int minutes) {
        return (hours * 60) + minutes;
    }
}
