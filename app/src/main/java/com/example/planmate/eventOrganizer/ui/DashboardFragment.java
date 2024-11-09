package com.example.planmate.eventOrganizer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;
import com.example.planmate.eventOrganizer.ui.createEvent.CreateEventActivity;
import com.example.planmate.eventOrganizer.ui.createEvent.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerViewUpcomingEvents;
    private FloatingActionButton fabCreateEvent;
    private FirebaseFirestore db;
    private List<Event> eventList;
    private UpcomingEventsAdapter adapter;
    private LinearLayout rsvpSummaryContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        db = FirebaseFirestore.getInstance();
        eventList = new ArrayList<>();

        // Set up RecyclerView for upcoming events
        recyclerViewUpcomingEvents = view.findViewById(R.id.recyclerViewUpcomingEvents);
        recyclerViewUpcomingEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UpcomingEventsAdapter(eventList);
        recyclerViewUpcomingEvents.setAdapter(adapter);

        // FloatingActionButton for creating a new event
        fabCreateEvent = view.findViewById(R.id.fabCreateEvent);
        fabCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivity(intent);
        });

        // RSVP Summary container
        rsvpSummaryContainer = view.findViewById(R.id.rsvpSummaryContainer);

        // Fetch data from Firestore
        fetchEventsFromFirestore();

        return view;
    }

    private void fetchEventsFromFirestore() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    rsvpSummaryContainer.removeAllViews(); // Clear RSVP summaries before populating

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        String eventId = document.getId(); // Get the document ID
                        eventList.add(event);
                        addRsvpSummaryCard(event, eventId); // Pass event and eventId to the method
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle the error if fetching events fails
                });
    }


    private void addRsvpSummaryCard(Event event, String eventId) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_rsvp_summary, rsvpSummaryContainer, false);
        TextView eventNameTextView = cardView.findViewById(R.id.eventName);
        TextView rsvpTextView = cardView.findViewById(R.id.rsvpSummary);

        // Set event name
        eventNameTextView.setText("Event Name: " + event.getEventName());

        // Initialize counters
        int[] accepted = {0};
        int[] declined = {0};
        int[] pending = {0};

        // Fetch RSVP counts from each user's `user_events` sub-collection
        db.collection("users")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot userDoc : usersSnapshot) {
                        userDoc.getReference().collection("user_events")
                                .whereEqualTo("eventId", eventId)
                                .get()
                                .addOnSuccessListener(userEventsSnapshot -> {
                                    for (DocumentSnapshot doc : userEventsSnapshot) {
                                        String status = doc.getString("status");
                                        if ("accepted".equalsIgnoreCase(status)) {
                                            accepted[0]++;
                                        } else if ("declined".equalsIgnoreCase(status)) {
                                            declined[0]++;
                                        } else {
                                            pending[0]++;
                                        }
                                    }

                                    // Update the RSVP summary text once all user documents have been processed
                                    String rsvpSummary = "RSVPs: " + accepted[0] + " Accepted, " + declined[0] + " Declined, " + pending[0] + " Pending";
                                    rsvpTextView.setText(rsvpSummary);
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to fetch RSVP data", Toast.LENGTH_SHORT).show());

        // Add the card view to the RSVP summary container
        rsvpSummaryContainer.addView(cardView);
    }


}
