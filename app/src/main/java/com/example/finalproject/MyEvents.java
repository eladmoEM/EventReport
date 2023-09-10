package com.example.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * The MyEvents fragment displays the events created by the current user.
 * It provides functionality to delete and edit events.
 */
public class MyEvents extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private EventDatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        recyclerView = view.findViewById(R.id.my_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), eventList);
        recyclerView.setAdapter(eventAdapter);

        databaseHelper = new EventDatabaseHelper(getContext());
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userId = SharedPrefManager.getInstance(getContext()).getLoggedInUserId();

        if (userId != null) {
            EventDatabaseHelper.setCurrentUserId(userId);
            fetchEvents(); // Fetch only the events created by the current user
        }

        eventAdapter.setOnDeleteClickListener(new EventAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                deleteEvent(position);
            }
        });

        eventAdapter.setOnEditClickListener(new EventAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(int position) {
                editEvent(position);
            }
        });

        return view;
    }

    /**
     * Retrieves the count of events in the list.
     *
     * @return The count of events.
     */
    public int getEventCount() {
        if (eventList != null) {
            return eventList.size();
        } else {
            return 0;
        }
    }

    /**
     * Fetches the events created by the current user from the database.
     * Updates the eventList and notifies the adapter of the changes.
     */
    private void fetchEvents() {
        String currentUserId = EventDatabaseHelper.getCurrentUserId(requireContext());
        Log.d("MyEvents", "Current User ID: " + currentUserId);
        if (currentUserId != null) {
            eventList.clear(); // Clear the existing list
            eventList.addAll(databaseHelper.getEventsCreatedByUser(currentUserId));
            eventAdapter.notifyDataSetChanged();
            Log.d("MyEvents", "Fetched events: " + eventList.size());
        } else {
            Log.d("MyEvents", "Current User ID is null");
            eventList.clear(); // Clear the list if the user ID is not available
            eventAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Deletes the event at the specified position.
     *
     * @param position The position of the event to delete.
     */
    private void deleteEvent(int position) {
        Event event = eventList.get(position);
        String eventId = event.getEventId();
        boolean isDeleted = databaseHelper.deleteEvent(eventId);
        if (isDeleted) {
            eventList.remove(position);
            eventAdapter.notifyItemRemoved(position);
            Toast.makeText(getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to delete event", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Edits the event at the specified position.
     *
     * @param position The position of the event to edit.
     */
    private void editEvent(int position) {
        Event event = eventList.get(position);
        String eventId = event.getEventId();
        boolean isDeleted = databaseHelper.deleteEvent(eventId);
        if (isDeleted) {
            // Create a new instance of the fragment
            AddNewEvent addNewEventFragment = new AddNewEvent();

            // Pass data to the fragment, if needed
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            addNewEventFragment.setArguments(args);

            // Perform the fragment transaction
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.container, addNewEventFragment)
                        .commit();
            }
        } else {
            Toast.makeText(getContext(), "Failed to edit event", Toast.LENGTH_SHORT).show();
        }
    }
}
