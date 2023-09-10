package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class HomePage extends AppCompatActivity implements HomePageAdapter.OnEventStatusChangedListener, AddNewEvent.OnEventUserIdListener {



    private static final String APPROVE_COUNT_KEY = "approve_count";
    private static final String DECLINE_COUNT_KEY = "decline_count";

    private RecyclerView recyclerView;
    private HomePageAdapter homePageAdapter;
    private List<Event> eventList;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment;
    private AddNewEvent addNewEvent;
    private UsersReports usersReports;
    private MyEvents myEvents;

    private EventDatabaseHelper databaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String userId;
    private SharedPreferences sharedPreferences;

    private PopupWindow sortPopupWindow;
    private Spinner spinnerEventType;
    private Spinner spinnerRegion;
    private Spinner spinnerRiskLevel;
    private Button btnCancel;
    private Button btnConfirm;
    private PopupWindow dateFilterPopupWindow;
    private Button btnSortOldToNew;
    private Button btnSortNewToOld;

    private String eventUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Initialize your fragments here, before setting up the bottom navigation view
        homeFragment = new HomeFragment();
        addNewEvent = new AddNewEvent();
        usersReports = new UsersReports();
        myEvents = new MyEvents();

        userId = getIntent().getStringExtra("userId");
        String eventUserId = getIntent().getStringExtra("eventUserId");

        homePageAdapter = new HomePageAdapter(eventUserId, this, eventList, userId, null, this);


        sharedPrefManager = SharedPrefManager.getInstance(this);
        sharedPreferences = sharedPrefManager.getSharedPreferences();
        userId = sharedPreferences.getString("user_id", null);

        databaseHelper = EventDatabaseHelper.getInstance(this);
        eventList = databaseHelper.getEventsNotActedUponByUser(userId);

        setupRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_menu_24);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.HomePage) {
                    switchFragment(homeFragment);
                    recyclerView.setVisibility(View.VISIBLE);
                    return true;
                } else if (id == R.id.add_event) {
                    switchFragment(addNewEvent);
                    recyclerView.setVisibility(View.GONE);
                    return true;
                } else if (id == R.id.users_reports) {
                    switchFragment(usersReports);
                    recyclerView.setVisibility(View.GONE);
                    return true;
                } else if (id == R.id.my_events) {
                    switchFragment(myEvents);
                    recyclerView.setVisibility(View.GONE);
                    return true;
                }

                return false;
            }
        });

        // The first displayed fragment is set here
        switchFragment(homeFragment);
        homePageAdapter.notifyDataSetChanged();

    }



    /**
     * Sets up the RecyclerView and its adapter.
     */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use your updated constructor with eventUserId
        homePageAdapter = new HomePageAdapter(eventUserId, this, eventList, userId, null, this);

        recyclerView.setAdapter(homePageAdapter);
    }



    /**
     * Switches the current fragment to the specified fragment.
     *
     * @param fragment The fragment to switch to.
     */
    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_option1) {
            // Create and show the date filter popup
            showDateFilterPopup();
            return true;
        } else if (id == R.id.menu_option2) {
            // Create and show the sort popup
            showSortPopup();
            return true;
        } else if (id == R.id.menu_option3) {
            // Head to about page
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the sort popup window.
     */
    private void showSortPopup() {
        View sortPopupView = LayoutInflater.from(this).inflate(R.layout.popup_sort, null);
        sortPopupWindow = new PopupWindow(sortPopupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        sortPopupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);

        // Initialize sort popup views
        spinnerEventType = sortPopupView.findViewById(R.id.spinner_event_type);
        spinnerRegion = sortPopupView.findViewById(R.id.spinner_region);
        spinnerRiskLevel = sortPopupView.findViewById(R.id.spinner_risk_level);
        btnCancel = sortPopupView.findViewById(R.id.btn_cancel);
        btnConfirm = sortPopupView.findViewById(R.id.btn_confirm);

        // Set click listeners for cancel and confirm buttons
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the sort popup
                sortPopupWindow.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected sort options
                String selectedEventType = spinnerEventType.getSelectedItem().toString();
                String selectedRegion = spinnerRegion.getSelectedItem().toString();
                String selectedRiskLevel = spinnerRiskLevel.getSelectedItem().toString();

                // Get the sort direction
                boolean isNewToOld = true; // Change this based on the selected sort option

                // Filter and sort events based on the selected sort options
                List<Event> filteredEvents = filterEvents(selectedEventType, selectedRegion, selectedRiskLevel, isNewToOld);
                eventList.clear();
                eventList.addAll(filteredEvents);
                homePageAdapter.notifyDataSetChanged();

                // Dismiss the sort popup
                sortPopupWindow.dismiss();
            }
        });

    }





    /**
     * Shows the date filter popup window.
     */
    private void showDateFilterPopup() {
        View dateFilterPopupView = LayoutInflater.from(this).inflate(R.layout.popup_date_filter, null);
        dateFilterPopupWindow = new PopupWindow(dateFilterPopupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        dateFilterPopupWindow.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);

        // Initialize date filter popup views
        btnSortOldToNew = dateFilterPopupView.findViewById(R.id.btn_sort_old_to_new);
        btnSortNewToOld = dateFilterPopupView.findViewById(R.id.btn_sort_new_to_old);

        // Set click listeners for sort buttons
        btnSortOldToNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sort events from old to new
                sortEventsOldToNew();
                dateFilterPopupWindow.dismiss();
            }
        });

        btnSortNewToOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sort events from new to old
                sortEventsNewToOld();
                dateFilterPopupWindow.dismiss();
            }
        });
    }

    /**
     * Sorts the events from old to new based on their dates.
     */
    private void sortEventsOldToNew() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event1.getDate().compareTo(event2.getDate());
            }
        });
        homePageAdapter.notifyDataSetChanged();
    }

    /**
     * Sorts the events from new to old based on their dates.
     */
    private void sortEventsNewToOld() {
        Collections.sort(eventList, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                return event2.getDate().compareTo(event1.getDate());
            }
        });
        homePageAdapter.notifyDataSetChanged();
    }

    /**
     * Filters the events based on the selected sort options.
     *
     * @param selectedEventType The selected event type.
     * @param selectedRegion    The selected region.
     * @param selectedRiskLevel The selected risk level.
     * @param isNewToOld        The sort direction (true for new to old, false for old to new).
     * @return The filtered and sorted list of events.
     */
    private List<Event> filterEvents(String selectedEventType, String selectedRegion, String selectedRiskLevel, boolean isNewToOld) {
        List<Event> filteredEvents = new ArrayList<>();

        // Apply the filter conditions
        for (Event event : eventList) {
            if (event.getEventType().equals(selectedEventType)
                    && event.getRegion().equals(selectedRegion)
                    && event.getRiskLevel().equals(selectedRiskLevel)) {
                filteredEvents.add(event);
            }
        }

        // Sort the events based on the date
        if (isNewToOld) {
            Collections.sort(filteredEvents, new Comparator<Event>() {
                @Override
                public int compare(Event event1, Event event2) {
                    return event2.getDate().compareTo(event1.getDate());
                }
            });
        } else {
            Collections.sort(filteredEvents, new Comparator<Event>() {
                @Override
                public int compare(Event event1, Event event2) {
                    return event1.getDate().compareTo(event2.getDate());
                }
            });
        }

        return filteredEvents;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    @Override
    public void onEventApproved(Event event) {
        showToast("Event Approved: " + event.getEventType());
        incrementCount(APPROVE_COUNT_KEY);

        databaseHelper = EventDatabaseHelper.getInstance(this);
        if (databaseHelper.recordEventStatus(event.getEventId(), userId, 1)) {
            eventList.remove(event);
            homePageAdapter.notifyDataSetChanged();
        } else {
            showToast("Error recording event approval status");
        }
    }

    @Override
    public void onEventDisapproved(Event event) {
        showToast("Event Disapproved: " + event.getEventType());
        incrementCount(DECLINE_COUNT_KEY);

        databaseHelper = EventDatabaseHelper.getInstance(this);
        if (databaseHelper.recordEventStatus(event.getEventId(), userId, -1)) {
            eventList.remove(event);
            homePageAdapter.notifyDataSetChanged();
        } else {
            showToast("Error recording event disapproval status");
        }
    }

    /**
     * Increments the count associated with the specified key in SharedPreferences.
     *
     * @param key The key of the count.
     */
    private void incrementCount(String key) {
        int count = sharedPreferences.getInt(key, 0);
        sharedPreferences.edit().putInt(key, count + 1).apply();
    }

    /**
     * Displays a short toast message with the specified message.
     *
     * @param message The message to display.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onEventUserIdReceived(String eventUserId) {
        // Save the eventUserId in shared preferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("eventUserId", eventUserId);
        editor.apply();

        // Now you can set the eventUserId to the adapter and notify data change
        homePageAdapter.setEventUserId(eventUserId);
        homePageAdapter.notifyDataSetChanged();

        // Start setting up your RecyclerView and adapter here...
    }


}
