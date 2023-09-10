/**
 * Adapter class for displaying events for MyEvents in a RecyclerView.
 */
package com.example.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;
    private OnDeleteClickListener onDeleteClickListener;
    private OnEditClickListener onEditClickListener;


    /**
     * Constructs a new EventAdapter.
     *
     * @param context    The context in which the adapter is being used.
     * @param eventList  The list of events to be displayed.
     */
    public EventAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    /**
     * Sets the listener for edit button clicks.
     *
     * @param onEditClickListener The listener to be set.
     */
    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    /**
     * Sets the listener for delete button clicks.
     *
     * @param onDeleteClickListener The listener to be set.
     */
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }


    /**
     * Called when RecyclerView needs a new {@link EventViewHolder} to represent an event view.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new EventViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.events, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (eventList == null || eventList.isEmpty()) {
            return;
        }

        Event event = eventList.get(position);
        holder.eventTypeTextView.setText(event.getEventType());
        holder.eventLocationTextView.setText(event.getLocation());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventRiskLevelTextView.setText(event.getRiskLevel());
        holder.eventRegionTextView.setText(event.getRegion());

        Bitmap eventImage = event.getEventImage();
        if (eventImage != null) {
            holder.eventImageView.setVisibility(View.VISIBLE);
            holder.eventImageView.setImageBitmap(eventImage);
        } else {
            holder.eventImageView.setVisibility(View.GONE);
        }

        holder.delete_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (onDeleteClickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(clickedPosition);
                }
            }
        });

        holder.edit_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (onEditClickListener != null && clickedPosition != RecyclerView.NO_POSITION) {
                    onEditClickListener.onEditClick(clickedPosition);
                }
            }
        });
    }

    /**
     * Returns the total number of events in the data set held by the adapter.
     *
     * @return The total number of events.
     */
    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    /**
     * Interface for handling delete button clicks.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    /**
     * Interface for handling edit button clicks.
     */
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    /**
     * ViewHolder class for holding event views.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTypeTextView;
        TextView eventLocationTextView;
        TextView eventDescriptionTextView;
        TextView eventRiskLevelTextView;
        TextView eventRegionTextView;
        ImageView eventImageView;
        Button delete_event;
        Button edit_event;

        /**
         * Constructs a new EventViewHolder.
         *
         * @param itemView The view associated with the ViewHolder.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTypeTextView = itemView.findViewById(R.id.event_type);
            eventLocationTextView = itemView.findViewById(R.id.event_location);
            eventDescriptionTextView = itemView.findViewById(R.id.event_description);
            eventRiskLevelTextView = itemView.findViewById(R.id.event_risk_level);
            eventRegionTextView = itemView.findViewById(R.id.event_region);
            eventImageView = itemView.findViewById(R.id.event_image_view);
            delete_event = itemView.findViewById(R.id.delete_event);
            edit_event = itemView.findViewById(R.id.edit_event);
        }
    }
}
