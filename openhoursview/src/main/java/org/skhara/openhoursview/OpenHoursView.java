package org.skhara.openhoursview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class OpenHoursView extends ExpandableListView {

    private int mHeight;
    private int mCount;

    public OpenHoursView(Context context) {
        super(context);
        initView();
    }

    public OpenHoursView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OpenHoursView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        super.setAdapter(adapter);
        mCount = adapter.getChildrenCount(0);
    }

    private void initView() {
        setDivider(null);
        setSelector(android.R.color.transparent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setIndicatorBoundsRelative(290 + 40, 0);
        }

        setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                ViewGroup.LayoutParams params = getLayoutParams();
                mHeight = getHeight();
                params.height = mCount <= 1 ? mHeight + mCount * mHeight :
                        mCount * mHeight - 15 * mCount;
                OpenHoursView.this.setLayoutParams(params);
            }
        });

        setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = mHeight;
                OpenHoursView.this.setLayoutParams(params);
            }
        });
    }
}
