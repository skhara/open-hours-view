package org.skhara.openhoursview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class OpenHoursAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private OpenHours mOpenHours;
    private String[] mDaysOfWeekArr;

    public OpenHoursAdapter(Context aContext, OpenHours aOpenHours) {
        mContext = aContext;
        mOpenHours = aOpenHours;
        mDaysOfWeekArr = mContext.getResources().getStringArray(R.array.oh_days_of_week);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return mOpenHours.getData().get(listPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_child, parent, false);
        }
        TextView childView = convertView.findViewById(R.id.expandedListItem);
        String text = mOpenHours.getData().get(expandedListPosition).trim();

        if (OpenHours.DAY_AND_NIGHT_MARKER.equals(text)) {
            childView.setText(mContext.getString(R.string.oh_day_and_night));
            return convertView;
        }

        if (OpenHours.CLOSE_MARKER.equals(text)) {
            text = mContext.getString(R.string.oh_day_closed);
        }

        childView.setText(getDecoratedChild(text, expandedListPosition));

        if (mOpenHours.isCurrentDay(expandedListPosition)) {
            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.RECTANGLE);
            int color = getColorFromRes(mOpenHours.getOpenState().mColorResId);
            background.setCornerRadius(25);
            background.setStroke(1, color);
            background.setColor(color);
            background.setAlpha(50);
            childView.setBackground(background);
        }

        return convertView;
    }

    private SpannableString getDecoratedChild(String aText, int aPosition) {
        aText = String.format(mDaysOfWeekArr[aPosition], aText);
        SpannableString ss = new SpannableString(aText);
        int color = getColorFromRes(android.R.color.holo_orange_light);
        ss.setSpan(new ForegroundColorSpan(color), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, 3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        return ss;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return mOpenHours.getData().size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return mOpenHours.getData();
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_group, parent, false);
        }

        decorateTitleView((TextView) convertView.findViewById(R.id.listTitle));

        return convertView;
    }

    private void decorateTitleView(TextView aTitleView) {
        OpenHours.OpenState state = mOpenHours.getOpenState();
        aTitleView.setText(state.mTextResId);
        Resources res = mContext.getResources();
        int color = getColorFromRes(state.mColorResId);
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            drawable = res.getDrawable(R.drawable.ic_access_time_white_24dp,
                    mContext.getTheme());
        } else {
            drawable = res.getDrawable(R.drawable.ic_access_time_white_24dp);
        }

        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        aTitleView.setCompoundDrawables(drawable, null, null, null);

        aTitleView.setTextColor(color);
        aTitleView.setTypeface(null, Typeface.BOLD);
    }

    private int getColorFromRes(int aColorResId) {
        Resources res = mContext.getResources();
        int color;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            color = res.getColor(aColorResId, mContext.getTheme());
        } else {
            color = res.getColor(aColorResId);
        }

        return color;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}