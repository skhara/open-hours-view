package org.skhara.openhoursview.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.skhara.openhoursview.OpenHours;
import org.skhara.openhoursview.OpenHoursAdapter;
import org.skhara.openhoursview.OpenHoursView;
import org.skhara.openhoursview.R;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private OpenHoursAdapter mAdapter;
    private OpenHours mOpenHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenHoursView view = findViewById(R.id.openHoursView);

        mOpenHours = new OpenHours(new ArrayList<String>() {
            {
                add("10:00 - 18:00");
                add("10:00 - 18:00");
                add("10:00 - 18:00");
                add(OpenHours.CLOSE_MARKER);
                add("10:00 - 18:00");
                add("10:00 - 18:00");
                add("10:00 - 18:00");
            }
        }, new Date());

        mAdapter = new OpenHoursAdapter(this, mOpenHours);
        view.setAdapter(mAdapter);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_button:
                mOpenHours.changeOpenState(OpenHours.OpenState.OPEN);
                break;

            case R.id.soon_close_button:
                mOpenHours.changeOpenState(OpenHours.OpenState.CLOSE_SOON);
                break;

            case R.id.close_button:
                mOpenHours.changeOpenState(OpenHours.OpenState.CLOSED);
                break;
        }

        mAdapter.notifyDataSetChanged();
    }
}
