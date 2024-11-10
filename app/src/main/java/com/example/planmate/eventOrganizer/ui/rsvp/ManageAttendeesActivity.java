package com.example.planmate.eventOrganizer.ui.rsvp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;
import com.example.planmate.eventOrganizer.ui.OrganizerActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ManageAttendeesActivity extends AppCompatActivity {

    private TextView acceptedCount, declinedCount, pendingCount;
    private RecyclerView attendeesRecyclerView;
    private Button addAttendeesButton, backButton;
    private FirebaseFirestore db;
    private String eventId;
    private String eventName;
    private AttendeesAdapter adapter;
    private List<Map<String, String>> attendeesList;
    private Set<String> addedUserIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_attendees);

        db = FirebaseFirestore.getInstance();

        // Retrieve event details from the intent
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");

        // Check if eventId is provided
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        acceptedCount = findViewById(R.id.acceptedCount);
        declinedCount = findViewById(R.id.declinedCount);
        pendingCount = findViewById(R.id.pendingCount);
        attendeesRecyclerView = findViewById(R.id.attendeesRecyclerView);
        addAttendeesButton = findViewById(R.id.addAttendeesButton);
        backButton = findViewById(R.id.backButton);

        attendeesList = new ArrayList<>();
        addedUserIds = new HashSet<>();
        adapter = new AttendeesAdapter(attendeesList);
        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeesRecyclerView.setAdapter(adapter);

        // Load RSVP summary and attendees list
        loadRSVPSummary();
        loadAttendees();

        // Add more attendees when button is clicked
        addAttendeesButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAttendeesActivity.this, SelectAttendeesActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Set up back button click listener
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageAttendeesActivity.this, OrganizerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data on resume to get the latest attendee list and RSVP summary
        loadRSVPSummary();
        loadAttendees();
    }

    private void loadRSVPSummary() {
        db.collection("users")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    AtomicInteger accepted = new AtomicInteger();
                    AtomicInteger declined = new AtomicInteger();
                    AtomicInteger pending = new AtomicInteger();

                    for (DocumentSnapshot userDoc : usersSnapshot) {
                        userDoc.getReference().collection("user_events")
                                .whereEqualTo("eventId", eventId)
                                .get()
                                .addOnSuccessListener(userEventsSnapshot -> {
                                    Log.d("RSVPSummary", "User events found: " + userEventsSnapshot.size());
                                    for (DocumentSnapshot doc : userEventsSnapshot) {
                                        String status = doc.getString("status");
                                        Log.d("RSVPSummary", "Status: " + status);
                                        if ("Accepted".equalsIgnoreCase(status)) {
                                            accepted.getAndIncrement();
                                        } else if ("Declined".equalsIgnoreCase(status)) {
                                            declined.getAndIncrement();
                                        } else {
                                            pending.getAndIncrement();
                                        }
                                    }
                                    // Update the TextViews with labels and counts
                                    acceptedCount.setText("Accepted: " + accepted.get());
                                    declinedCount.setText("Declined: " + declined.get());
                                    pendingCount.setText("Pending: " + pending.get());
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load RSVP summary", Toast.LENGTH_SHORT).show());
    }

    private void loadAttendees() {
        attendeesList.clear();
        addedUserIds.clear(); // Clear the set to ensure no duplicates on reload

        db.collection("users")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot userDoc : usersSnapshot) {
                        String userName = userDoc.getString("username");
                        String userId = userDoc.getId();

                        // Access the user's user_events sub-collection
                        userDoc.getReference().collection("user_events")
                                .whereEqualTo("eventId", eventId)
                                .get()
                                .addOnSuccessListener(userEventsSnapshot -> {
                                    for (DocumentSnapshot doc : userEventsSnapshot) {
                                        String status = doc.getString("status");

                                        // Only add unique user entries
                                        if (addedUserIds.add(userId)) { // add() returns false if already present
                                            Map<String, String> attendee = new HashMap<>();
                                            attendee.put("userName", userName != null ? userName : "Unknown User");
                                            attendee.put("status", status != null ? status : "Pending");
                                            attendeesList.add(attendee);
                                        }
                                    }
                                    // Notify adapter of the changes after the data is completely loaded
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load user events", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show());
    }
}
