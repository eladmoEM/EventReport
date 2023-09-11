
package com.example.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

import java.util.Date;

public class AddNewEvent extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

    private Spinner riskLevelSpinner;
    private Button addNewEventButton;
    private Spinner eventTypeSpinner;
    private EditText locationEditText;
    private EditText descriptionEditText;
    private ImageView eventImageView;
    private Button imageButton;
    private Spinner regionSpinner;

    private Bitmap eventImage;

    /**
     * Inflates the layout for this fragment and initializes the UI elements.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_new_event, container, false);

        riskLevelSpinner = rootView.findViewById(R.id.risk_level_spinner);
        eventTypeSpinner = rootView.findViewById(R.id.event_type_spinner);
        locationEditText = rootView.findViewById(R.id.location_editText);
        descriptionEditText = rootView.findViewById(R.id.description_editText);
        addNewEventButton = rootView.findViewById(R.id.add_button);
        eventImageView = rootView.findViewById(R.id.event_image_view);
        imageButton = rootView.findViewById(R.id.image_button);
        regionSpinner = rootView.findViewById(R.id.region_spinner);

        populateRiskLevelSpinner();
        populateEventTypeSpinner();
        populateEventRegion();

        addNewEventButton.setOnClickListener(view -> {
            if (isFormValid()) {
                moveToNextPage();
            } else {
                Toast.makeText(requireContext(), "Please fill up all the fields.", Toast.LENGTH_SHORT).show();
            }
        });

        imageButton.setOnClickListener(view -> {
            // Check camera permission
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        return rootView;
    }

    /**
     * Populates the risk level spinner with values from resources.
     */
    private void populateRiskLevelSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.risk_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        riskLevelSpinner.setAdapter(adapter);
    }

    /**
     * Populates the event type spinner with values from resources.
     */
    private void populateEventTypeSpinner() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.event_type, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(arrayAdapter);
    }

    /**
     * Populates the event region spinner with values from resources.
     */
    private void populateEventRegion() {
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.event_region, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(arrayAdapter);
    }

    /**
     * Checks if all form fields are filled.
     *
     * @return true if all fields are filled, false otherwise.
     */
    private boolean isFormValid() {
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String riskLevel = riskLevelSpinner.getSelectedItem().toString().trim();
        String eventType = eventTypeSpinner.getSelectedItem().toString().trim();
        String region = regionSpinner.getSelectedItem().toString().trim();
        return !location.isEmpty() && !description.isEmpty() && !riskLevel.equals("Choose Risk Level") && !eventType.equals("Choose Event Type") && !region.equals("Choose Region");
    }

    /**
     * Moves to the next page after adding the event to the database.
     */
    private void moveToNextPage() {
        String eventType = eventTypeSpinner.getSelectedItem().toString();
        String location = locationEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String riskLevel = riskLevelSpinner.getSelectedItem().toString();
        String region = regionSpinner.getSelectedItem().toString();
        Date date = new Date();

        // Get the current user ID from the SharedPrefManager
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        String currentUserId = sharedPrefManager.getLoggedInUserId();

        // Convert eventImage Bitmap to byte array using Utils.getBytes()
        byte[] eventImageBytes = null;
        if (eventImage != null) {
            eventImageBytes = Utils.getBytes(eventImage);
        }

        EventDatabaseHelper databaseHelper = EventDatabaseHelper.getInstance(requireContext());

        // Pass the currentUserId to generate a unique eventId based on the user's ID
        String eventId = EventDatabaseHelper.generateUniqueId(currentUserId);

        Log.d("moveToNextPage", "currentUserId: " + currentUserId);
        Event event = new Event(eventId, currentUserId, eventType, location, description, riskLevel, region, eventImage, date);
        Log.d("moveToNextPage", "new event's userId: " + event.getUserId());

        boolean eventAdded = databaseHelper.addEvent(event);

        if (eventAdded) {
            Toast.makeText(requireContext(), "Event added successfully", Toast.LENGTH_SHORT).show();

            // Save the event's userId to SharedPreferences here
            sharedPrefManager.saveEventCreatorId(event.getUserId());

            // Pass the eventUserId back to the activity
            ((OnEventUserIdListener) requireActivity()).onEventUserIdReceived(event.getUserId());

            // Start the homepage activity
            Intent intent = new Intent(requireContext(), HomePage.class);
            intent.putExtra("eventUserId", event.getUserId());
            startActivity(intent);

            // Finish the current activity
            requireActivity().finish();

            Log.d("HomePageAdapter", "UserId: " + currentUserId + ", Event Creator Id: " + event.getUserId());
        } else {
            Toast.makeText(requireContext(), "Event already exists", Toast.LENGTH_SHORT).show();
        }

    }



    /**
     * Opens the camera to capture an image.
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the result of the camera activity.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                eventImage = (Bitmap) extras.get("data");
                eventImageView.setImageBitmap(eventImage);
            }
        }
    }

    public interface OnEventUserIdListener {
        void onEventUserIdReceived(String eventUserId);
    }
}
