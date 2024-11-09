package com.example.planmate.eventOrganizer.ui.rsvp;

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

import com.example.planmate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.atomic.AtomicInteger;

public class ManageRSVPFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LinearLayout rsvpSummaryContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_rsvp, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        rsvpSummaryContainer = view.findViewById(R.id.rsvpSummaryContainer);

        String userUid = auth.getCurrentUser().getUid();
        loadUserEvents(userUid);

        return view;
    }

    private void loadUserEvents(String userUid) {
        db.collection("events")
                .whereEqualTo("organizerId", userUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot eventDoc : querySnapshot) {
                        String eventId = eventDoc.getId();
                        String eventName = eventDoc.getString("eventName");
                        addRsvpSummaryCard(eventName, eventId);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show());
    }

    private void addRsvpSummaryCard(String eventName, String eventId) {
        View cardView = LayoutInflater.from(getContext()).inflate(R.layout.item_rsvp_summary, rsvpSummaryContainer, false);
        TextView eventNameTextView = cardView.findViewById(R.id.eventName);
        TextView rsvpTextView = cardView.findViewById(R.id.rsvpSummary);

        // Set event name
        eventNameTextView.setText("Event Name: " + eventName);

        AtomicInteger accepted = new AtomicInteger();
        AtomicInteger declined = new AtomicInteger();
        AtomicInteger pending = new AtomicInteger();

        // Fetch RSVP counts from each user's `user_events` sub-collection for this event
        db.collection("users")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (QueryDocumentSnapshot userDoc : usersSnapshot) {
                        userDoc.getReference().collection("user_events")
                                .whereEqualTo("eventId", eventId)
                                .get()
                                .addOnSuccessListener(userEventsSnapshot -> {
                                    for (QueryDocumentSnapshot doc : userEventsSnapshot) {
                                        String status = doc.getString("status");
                                        if ("Accepted".equalsIgnoreCase(status)) accepted.getAndIncrement();
                                        else if ("Declined".equalsIgnoreCase(status)) declined.getAndIncrement();
                                        else pending.getAndIncrement();
                                    }
                                    // Update the RSVP summary text
                                    String rsvpSummary = "RSVPs: " + accepted.get() + " Accepted, " + declined.get() + " Declined, " + pending.get() + " Pending";
                                    rsvpTextView.setText(rsvpSummary);
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load RSVP data", Toast.LENGTH_SHORT).show());

        // Set the card to be clickable
        cardView.setOnClickListener(v -> {
            // Navigate to ManageAttendeesActivity and pass event details
            Intent intent = new Intent(getContext(), ManageAttendeesActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        });

        // Add the card view to the RSVP summary container
        rsvpSummaryContainer.addView(cardView);
    }

}
