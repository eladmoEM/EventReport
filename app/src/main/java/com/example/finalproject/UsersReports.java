package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/**
 * The UsersReports fragment displays the reports submitted by other users.
 * It implements the OnEventStatusChangedListener to handle event approval and disapproval.
 */
public class UsersReports extends Fragment implements HomePageAdapter.OnEventStatusChangedListener {

    private RecyclerView recyclerView;
    private HomePageAdapter homePageAdapter;
    private List<Event> eventList;
    private EventDatabaseHelper databaseHelper;
    private String userId;

    private String eventUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_reports, container, false);

        recyclerView = view.findViewById(R.id.users_reports_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
        userId = sharedPrefManager.getLoggedInUserId();

        homePageAdapter = new HomePageAdapter(eventUserId, requireContext(), eventList, userId, null, this);
        recyclerView.setAdapter(homePageAdapter);

        databaseHelper = EventDatabaseHelper.getInstance(getContext());

        if (userId != null) {
            List<Event> otherUserEvents = databaseHelper.getEventsByOtherUsers(userId);
            eventList.addAll(otherUserEvents);
            homePageAdapter.notifyDataSetChanged();
        }

        return view;
    }

    /**
     * Handles event approval.
     *
     * @param event The approved event.
     */
    @Override
    public void onEventApproved(Event event) {
        Toast.makeText(getContext(), "Event Approved: " + event.getEventType(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles event disapproval.
     *
     * @param event The disapproved event.
     */
    @Override
    public void onEventDisapproved(Event event) {
        Toast.makeText(getContext(), "Event Disapproved: " + event.getEventType(), Toast.LENGTH_SHORT).show();
    }
}
