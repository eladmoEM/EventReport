package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class provides an interface to display event details such as
 * number of events, number of approved events and number of declined events.
 */
public class EventsInfo extends AppCompatActivity {

    private static final String APPROVE_COUNT_KEY = "approve_count";
    private static final String DECLINE_COUNT_KEY = "decline_count";

    private TextView numEventsTextView;
    private TextView numEventsApproved;
    private TextView numEventDecline;

    private MyEvents myEventsFragment;

    private SharedPrefManager sharedPrefManager;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_info);

        sharedPrefManager = SharedPrefManager.getInstance(this);
        sharedPreferences = sharedPrefManager.getSharedPreferences();

        numEventsTextView = findViewById(R.id.numEvents);
        numEventsApproved = findViewById(R.id.numApprove);
        numEventDecline = findViewById(R.id.numDecline);

        myEventsFragment = new MyEvents();

        getSupportFragmentManager().beginTransaction()
                .add(myEventsFragment, "MyEventsFragment")
                .commit();
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
     * for your activity to start interacting with the user. This is a good place
     * to begin animations, open exclusive-access devices (such as the camera), etc.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateEventCount();
        updateApproveDeclineCounts();
    }

    /**
     * Updates the total event count shown in the UI from the data obtained from the fragment.
     */
    private void updateEventCount() {
        int eventCount = myEventsFragment.getEventCount();
        numEventsTextView.setText(String.valueOf(eventCount));
    }

    /**
     * Updates the counts of approved and declined events shown in the UI from the data
     * stored in shared preferences.
     */
    private void updateApproveDeclineCounts() {
        int approveCount = sharedPreferences.getInt(APPROVE_COUNT_KEY, 0);
        int declineCount = sharedPreferences.getInt(DECLINE_COUNT_KEY, 0);

        numEventsApproved.setText(String.valueOf(approveCount));
        numEventDecline.setText(String.valueOf(declineCount));
    }

}
