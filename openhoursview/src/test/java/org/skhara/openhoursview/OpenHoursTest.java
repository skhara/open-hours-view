package org.skhara.openhoursview;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.skhara.openhoursview.OpenHours.CLOSE_MARKER;
import static org.skhara.openhoursview.OpenHours.DAY_AND_NIGHT_MARKER;
import static org.skhara.openhoursview.OpenHours.OpenState.CLOSED;
import static org.skhara.openhoursview.OpenHours.OpenState.CLOSE_SOON;
import static org.skhara.openhoursview.OpenHours.OpenState.OPEN;

public class OpenHoursTest {

    private static final int MON = 2;
    private static final int TUE = 3;
    private static final int WED = 4;
    private static final int THU = 5;
    private static final int FRI = 6;
    private static final int SAT = 7;
    private static final int SUN = 1;

    private Calendar mCalendar;
    private List<String> mData;
    @Before
    public void setUp() {
        mCalendar = GregorianCalendar.getInstance();
        mCalendar.setTime(new Date());
    }

    @Test
    public void weekDayTest() {
        mData = new ArrayList<>();
        mData.add("10:00 - 18:00"); // Mon
        mData.add("10:00 - 18:00"); // Tue
        mData.add("10:00 - 18:00"); // Wed
        mData.add("10:00 - 18:00"); // Thu
        mData.add("10:00 - 18:00"); // Fri
        mData.add("10:00 - 18:00"); // Sat
        mData.add("10:00 - 18:00"); // Sun

        for (int i = 1; i <= 7; i++) {
            assertStateForDayEquals(i);
        }
    }

    private void assertStateForDayEquals(int aDayOfWeek) {
        assertStateEquals(aDayOfWeek, 10, 0, OPEN);
        assertStateEquals(aDayOfWeek, 9, 59, CLOSED);
        assertStateEquals(aDayOfWeek, 10, 1, OPEN);
        assertStateEquals(aDayOfWeek, 17, 29, OPEN);
        assertStateEquals(aDayOfWeek, 17, 30, CLOSE_SOON);
        assertStateEquals(aDayOfWeek, 17, 59, CLOSE_SOON);
        assertStateEquals(aDayOfWeek, 18, 0, CLOSED);
        assertStateEquals(aDayOfWeek, 18, 1, CLOSED);
    }

    @Test
    public void afterMidnightTest() {
        mData = new ArrayList<>();
        mData.add("10:00 - 5:00");  // Mon
        mData.add("10:00 - 18:00"); // Tue...Sun

        assertMidnightStateEquals(MON, TUE);
    }

    @Test
    public void sunMonAfterMidnightTest() {
        mData = new ArrayList<>();
        mData.add("10:00 - 18:00"); // Mon
        mData.add("10:00 - 18:00");
        mData.add("10:00 - 18:00");
        mData.add("10:00 - 18:00");
        mData.add("10:00 - 18:00");
        mData.add("10:00 - 18:00");
        mData.add("10:00 - 5:00"); // Sun

        assertMidnightStateEquals(SUN, MON);
    }

    private void assertMidnightStateEquals(int aPrevDay, int aNextDay) {
        assertStateEquals(aPrevDay, 10, 0, OPEN);
        assertStateEquals(aPrevDay, 24, 0, OPEN);
        assertStateEquals(aNextDay, 4, 29, OPEN);
        assertStateEquals(aNextDay, 4, 30, CLOSE_SOON);
        assertStateEquals(aNextDay, 4, 59, CLOSE_SOON);
        assertStateEquals(aNextDay, 5, 0, CLOSED);
        assertStateEquals(aNextDay, 9, 59, CLOSED);
        assertStateEquals(aNextDay, 10, 0, OPEN);
    }

    @Test
    public void closeDaysTest() {
        mData = new ArrayList<>();
        mData.add("closed"); // Mon
        mData.add("10:00 - 18:00"); // Tue
        mData.add("10:00 - 18:00"); // Wed
        mData.add(CLOSE_MARKER); // Thu
        mData.add("10:00 - 18:00"); // Fri
        mData.add("10:00 - 18:00"); // Sat
        mData.add("10:00 - 18:00"); // Sun

        assertStateEquals(MON, 10, 0, CLOSED);
        assertStateEquals(TUE, 10, 0, OPEN);
        assertStateEquals(WED, 10, 0, OPEN);
        assertStateEquals(THU, 10, 0, CLOSED);
        assertStateEquals(SUN, 9, 59, CLOSED);
    }

    @Test
    public void zeroBoundsTest() {
        mData = new ArrayList<>();
        mData.add("23:30 - 00:30"); // Mon
        mData.add("01:00 - 03:00"); // Tue...Sun

        assertStateEquals(MON, 23, 59, OPEN);
        assertStateEquals(MON, 24, 0, CLOSE_SOON);
        assertStateEquals(TUE, 0, 0, CLOSE_SOON);
        assertStateEquals(TUE, 0, 0, CLOSE_SOON);
        assertStateEquals(TUE, 0, 29, CLOSE_SOON);
        assertStateEquals(TUE, 0, 30, CLOSED);
        assertStateEquals(TUE, 1, 0, OPEN);
    }

    @Test
    public void dayAndNightTest() {
        mData = new ArrayList<>();
        mData.add("00:00 - 00:00"); // Mon
        mData.add("00:00 - 00:00"); // Tue
        mData.add("00:00 - 00:00"); // Wed
        mData.add(CLOSE_MARKER); // Thu
        mData.add("00:00 - 00:00"); // Fri...Sun

        assertStateEquals(MON, 0, 0, OPEN);
        assertStateEquals(MON, 23, 59, OPEN);
        assertStateEquals(MON, 24, 0, OPEN);
        assertStateEquals(TUE, 0, 0, OPEN);
        assertStateEquals(WED, 0, 0, OPEN);
        assertStateEquals(WED, 24, 0, CLOSED);
        assertStateEquals(THU, 0, 0, CLOSED);
        assertStateEquals(THU, 23, 59, CLOSED);
        assertStateEquals(THU, 24, 0, OPEN);
    }

    @Test
    public void testOpenForNullData() {
        mData = null;
        assertStateEquals(MON, 12, 0, OPEN);
        assertStateEquals(TUE, 12, 0, OPEN);

        mData = new ArrayList<>();
        assertStateEquals(MON, 12, 0, OPEN);
        assertStateEquals(TUE, 12, 0, OPEN);
    }

    @Test
    public void testDayAndNight() {
        mData = new ArrayList<>();
        mData.add(DAY_AND_NIGHT_MARKER);
        assertStateEquals(MON, 12, 0, OPEN);
        assertStateEquals(TUE, 12, 0, OPEN);

        mData = new ArrayList<>();
        assertStateEquals(MON, 12, 0, OPEN);
        assertStateEquals(TUE, 12, 0, OPEN);
    }

    @Test
    public void testShortData() {
        mData = new ArrayList<>();
        mData.add("10:00 - 18:00"); // Mon...Sun

        for (int i = 1; i <= 7; i++) {
            assertStateEquals(i, 12, 0, OPEN);
            assertStateEquals(i, 18, 0, CLOSED);
            assertStateEquals(i, 17, 59, CLOSE_SOON);
        }

        assertStateEquals(MON, 9, 59, CLOSED);

        mData = new ArrayList<>();
        mData.add("10:00 - 18:00"); // Mon
        mData.add("10:00 - 5:00"); // Tue...Sun

        assertStateEquals(TUE, 4, 29, CLOSED);
        assertStateEquals(MON, 4, 29, OPEN);
    }

    private void assertStateEquals(int aDayOfWeek, int aHour, int aMinute,
                                   OpenHours.OpenState aState) {
        mCalendar.set(Calendar.HOUR_OF_DAY, aHour);
        mCalendar.set(Calendar.MINUTE, aMinute);
        mCalendar.set(Calendar.DAY_OF_WEEK, aDayOfWeek);

        OpenHours openHours = new OpenHours(mData, mCalendar.getTime());
        assertEquals(aState, openHours.getOpenState());
    }
}