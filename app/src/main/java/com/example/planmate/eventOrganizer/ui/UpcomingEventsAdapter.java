package com.example.planmate.eventOrganizer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;
import com.example.planmate.eventOrganizer.ui.createEvent.Event;

import java.util.List;

public class UpcomingEventsAdapter extends RecyclerView.Adapter<UpcomingEventsAdapter.ViewHolder> {

    private final List<Event> events;

    public UpcomingEventsAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_organizer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate());
        holder.eventLocation.setText(event.getEventLocation());
        holder.eventDescription.setText(event.getEventDescription());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventDate, eventLocation, eventDescription;

        ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.organizerEventName);
            eventDate = itemView.findViewById(R.id.organizerEventDate);
            eventLocation = itemView.findViewById(R.id.organizerEventLocation);
            eventDescription = itemView.findViewById(R.id.organizerEventDescription);
        }
    }
}
