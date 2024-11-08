package com.example.planmate.user.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;
import com.example.planmate.user.ui.event.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> eventList;

    // Constructor
    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    public void setEventList(List<Event> events) {
        this.eventList = events;
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each event
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        // Get the event at the current position
        Event event = eventList.get(position);
        // Bind the event data to the view holder
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate());
        holder.eventLocation.setText(event.getEventLocation());
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;  // Return the number of events
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find the views in the item layout
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventLocation = itemView.findViewById(R.id.event_location);
        }
    }
}
