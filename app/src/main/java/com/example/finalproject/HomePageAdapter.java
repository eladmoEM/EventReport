package com.example.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * The HomePageAdapter class is responsible for binding event data to the RecyclerView in the home page.
 * It also provides functionality to handle event status changes and event deletion.
 */
public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {

    private Context context;
    private List<Event> eventList;
    private OnDeleteEventListener onDeleteEventListener;
    private OnEventStatusChangedListener eventStatusChangedListener;

    private List<String> comments;

    private String userId;

    private AddNewEvent addNewEvent;

    private String eventUserId;


    /**
     * Constructs a new HomePageAdapter with the specified parameters.
     *
     * @param context                   The context.
     * @param eventList                 The list of events.
     * @param onDeleteEventListener     The listener for event deletion.
     * @param eventStatusChangedListener The listener for event status changes.
     */
    public HomePageAdapter(String eventUserId,Context context, List<Event> eventList, String userId,
                           OnDeleteEventListener onDeleteEventListener,
                           OnEventStatusChangedListener eventStatusChangedListener) {

        this.context = context;
        this.eventList = eventList;
        this.userId = userId;
        this.onDeleteEventListener = onDeleteEventListener;
        this.eventStatusChangedListener = eventStatusChangedListener;
        this.addNewEvent = new AddNewEvent();
        this.eventUserId = eventUserId;

    }

    /**
     * Sets the listener for event status changes.
     *
     * @param listener The listener to set.
     */
    public void setOnEventStatusChangedListener(OnEventStatusChangedListener listener) {
        this.eventStatusChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.approve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(context);
        this.userId = sharedPrefManager.getLoggedInUserId();

        String eventCreatorId = sharedPrefManager.getEventCreatorId();
        Log.d("HomePageAdapter", "Stored Event Creator Id: " + eventCreatorId);


        Log.d("HomePageAdapter", "Logged-in user ID: " + userId);


        boolean createdByLogged = userId.equals(eventCreatorId);


        List<String> comments = new ArrayList<>();
        CommentsAdapter adapter = new CommentsAdapter(comments);
        holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.commentsRecyclerView.setAdapter(adapter);

        // Check if the event was created by the logged-in user
        if (createdByLogged) {
            // If the event was created by the logged-in user, hide the buttons
            holder.approveButton.setVisibility(View.GONE);
            holder.disapproveButton.setVisibility(View.GONE);
            holder.addCommentButton.setVisibility(View.GONE);
        } else {
            // If the event was created by other users, show the buttons
            holder.approveButton.setVisibility(View.VISIBLE);
            holder.disapproveButton.setVisibility(View.VISIBLE);
            holder.addCommentButton.setVisibility(View.VISIBLE);
        }


        holder.eventTypeTextView.setText(event.getEventType());
        holder.eventRiskLevelTextView.setText(event.getRiskLevel());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventRegionTextView.setText(event.getRegion());

        Bitmap eventImage = event.getEventImage();
        if (eventImage != null) {
            holder.eventImageView.setVisibility(View.VISIBLE);
            holder.eventImageView.setImageBitmap(eventImage);
        } else {
            holder.eventImageView.setVisibility(View.GONE);
        }

        // Set click listeners for the buttons
        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventStatusChangedListener != null) {
                    eventStatusChangedListener.onEventApproved(event);
                }
            }
        });

        holder.disapproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventStatusChangedListener != null) {
                    eventStatusChangedListener.onEventDisapproved(event);
                }
            }
        });

        holder.addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View dialogView = inflater.inflate(R.layout.dialog_add_comment, null);
                builder.setView(dialogView);

                final EditText commentInput = (EditText) dialogView.findViewById(R.id.comment_input);

                builder.setTitle("Add Comment")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String commentText = commentInput.getText().toString();
                                if (!commentText.trim().isEmpty()) {
                                    // Add the comment to the list
                                    comments.add(commentText);
                                    // Update the adapter with the new comments list
                                    adapter.updateComments(comments);
                                    // Logging
                                    Log.i("CommentDialog", "Comment added: " + commentText);
                                    Log.i("CommentDialog", "Comments list size: " + comments.size());
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            }
        });




        holder.btn_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.commentsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        holder.btn_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.commentsRecyclerView.setVisibility(View.GONE);
            }
        });


    }


        @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void setEventUserId(String eventUserId) {
        this.eventUserId = eventUserId;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImageView;
        TextView eventTypeTextView;
        TextView eventRiskLevelTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        TextView eventRegionTextView;
        Button approveButton;
        Button disapproveButton;
        Button addCommentButton;
        Button btn_comments;
        Button btn_hide;
        RecyclerView commentsRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImageView = itemView.findViewById(R.id.event_image_view);
            eventTypeTextView = itemView.findViewById(R.id.event_type);
            eventRiskLevelTextView = itemView.findViewById(R.id.event_risk_level);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventLocationTextView = itemView.findViewById(R.id.event_location);
            eventRegionTextView = itemView.findViewById(R.id.event_region);
            approveButton = itemView.findViewById(R.id.approve);
            disapproveButton = itemView.findViewById(R.id.disapprove);
            addCommentButton = itemView.findViewById(R.id.add_comment_button);
            btn_comments = itemView.findViewById(R.id.btn_comments);
            btn_hide = itemView.findViewById(R.id.btn_hide);
            commentsRecyclerView = itemView.findViewById(R.id.comments_recycler_view);
        }
    }

    /**
     * The interface for event deletion callbacks.
     */
    public interface OnDeleteEventListener {
        void onDeleteEvent(Event event);
    }

    /**
     * The interface for event status change callbacks.
     */
    public interface OnEventStatusChangedListener {
        void onEventApproved(Event event);
        void onEventDisapproved(Event event);
    }


}
