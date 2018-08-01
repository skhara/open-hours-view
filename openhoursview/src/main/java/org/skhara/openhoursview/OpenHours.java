package org.skhara.openhoursview;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.skhara.openhoursview.DateUtil.MINUTES_PER_DAY;

public class OpenHours {
    public static final String CLOSE_MARKER = "closed";
    public static final String DAY_AND_NIGHT_MARKER = "day_and_night";

    private static final String OPEN_CLOSE_INTERVAL_DELIMITER = "[-–]";
    private static final String HOUR_MINUTE_DELIMITER = ":";
    private static final int MINUTES_PER_HOUR = 60;
    private static final int NEAR_TO_CLOSE_INTERVAL = 30;

    private int mCurrentIndex;
    private final Date mDate;
    private final List<String> mOpenHours;

    private OpenState mOpenState = OpenState.CLOSED;

    private int mCurrentMinute;
    private int mStartMinute;
    private int mEndMinute;

    public OpenHours(List<String> aOpenHours, Date aDate) {
        mDate = aDate;
        mOpenHours = aOpenHours;

        if (aOpenHours == null || aOpenHours.size() == 0) {
            mOpenState = OpenState.OPEN;
            return;
        }

        if (aOpenHours.size() == 1) {
            if (DAY_AND_NIGHT_MARKER.equals(aOpenHours.get(0))) {
                mOpenState = OpenState.OPEN;
                return;
            }
        }

        expandDataIfNeeded();

        mCurrentMinute = getCurrentMinute();
        mCurrentIndex = DateUtil.getDayOfWeekIndex(aDate);
        parseOpenHours(mCurrentIndex);
        setupOpenState();
    }

    private void expandDataIfNeeded() {
        int size = mOpenHours.size();
        String prevData = mOpenHours.get(size - 1);
        for (int i = size; i < 7; i++) {
            mOpenHours.add(prevData);
        }
    }

    private void parseOpenHours(int aIndex) {

        String openHours = mOpenHours.get(aIndex);
        if (openHours.contains(CLOSE_MARKER)) {
            mStartMinute = -1;
            return;
        }

        final String[] parts = openHours.split(OPEN_CLOSE_INTERVAL_DELIMITER);
        String startTime = parts[0];
        mStartMinute = getMinute(startTime);

        if (parts.length > 1) {
            String endTime = parts[1];
            mEndMinute = getMinute(endTime);
            if (mStartMinute != 0 && mEndMinute == 0) {
                mEndMinute = MINUTES_PER_DAY;
            }
        } else if (mStartMinute != 0) {
            mEndMinute = MINUTES_PER_DAY;
        }

        if (mStartMinute > mEndMinute) {
            mEndMinute = mEndMinute + MINUTES_PER_DAY;
        }
    }

    private void setupOpenState() {
        if (mStartMinute == -1) {
            return;
        }

        if (mStartMinute == 0 && mEndMinute == 0) {
            mOpenState = OpenState.OPEN;
            return;
        }

        if (mCurrentMinute < mStartMinute) {
            mCurrentMinute = mCurrentMinute + MINUTES_PER_DAY;
            parseOpenHours(DateUtil.getPreviousIndex(mCurrentIndex));
            setupOpenState();

        } else {
            if (mCurrentMinute < mEndMinute) {
                if (mCurrentMinute >= mEndMinute - NEAR_TO_CLOSE_INTERVAL) {
                    mOpenState = OpenState.CLOSE_SOON;
                } else {
                    mOpenState = OpenState.OPEN;
                }
            }
        }
    }

    private int getCurrentMinute() {
        final Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(mDate);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        return DateUtil.inMinutes(currentHour, calendar.get(Calendar.MINUTE));
    }

    /**
     * parse the minute of day for the given string
     * "5:30" returns 5*60 + 30
     */
    private int getMinute(String time) {
        if (time == null || time.isEmpty()) {
            return 0;
        }
        int hour = 0;
        int minute = 0;
        try {
            if (time.contains(HOUR_MINUTE_DELIMITER)) {
                final String[] parts = time.split(HOUR_MINUTE_DELIMITER);
                hour = Integer.parseInt(parts[0].trim());
                minute = Integer.parseInt(parts[1].trim());
            } else {
                hour = Integer.parseInt(time.trim());
            }
        } catch (Exception ignored) {
        }

        return hour * MINUTES_PER_HOUR + minute;
    }

    public List<String> getData() {
        return mOpenHours;
    }

    public OpenState getOpenState() {
        return mOpenState;
    }

    public boolean isCurrentDay(int aPosition) {
        return DateUtil.getDayOfWeekIndex(mDate) == aPosition;
    }

    public void changeOpenState(OpenState aState) {
        mOpenState = aState;
    }

    public enum OpenState {
        OPEN(R.string.oh_state_open, android.R.color.holo_green_light),
        CLOSE_SOON(R.string.oh_state_close_soon, android.R.color.holo_orange_light),
        CLOSED(R.string.oh_state_close, android.R.color.holo_red_light);

        public int mTextResId;
        public int mColorResId;

        OpenState(int aTextResId, int aColorResId) {
            mTextResId = aTextResId;
            mColorResId = aColorResId;
        }
    }
}
