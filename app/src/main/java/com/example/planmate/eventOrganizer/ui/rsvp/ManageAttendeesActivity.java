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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ManageAttendeesActivity extends AppCompatActivity {

    private TextView acceptedCount, declinedCount, pendingCount;
    private RecyclerView attendeesRecyclerView;
    private Button addAttendeesButton, backButton;
    private FirebaseFirestore db;
    private String eventId;
    private String eventName;

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

        attendeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                                    runOnUiThread(() -> {
                                        acceptedCount.setText("Accepted: " + accepted.get());
                                        declinedCount.setText("Declined: " + declined.get());
                                        pendingCount.setText("Pending: " + pending.get());
                                    });
                                });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load RSVP summary", Toast.LENGTH_SHORT).show());
    }

    private void loadAttendees() {
        List<Map<String, String>> attendeesList = new ArrayList<>();

        db.collection("users")
                .get()
                .addOnSuccessListener(usersSnapshot -> {
                    for (DocumentSnapshot userDoc : usersSnapshot) {
                        // Retrieve the username from the parent user document
                        String userName = userDoc.getString("username");

                        // Access the user's user_events sub-collection
                        userDoc.getReference().collection("user_events")
                                .whereEqualTo("eventId", eventId)
                                .get()
                                .addOnSuccessListener(userEventsSnapshot -> {
                                    for (DocumentSnapshot doc : userEventsSnapshot) {
                                        // Retrieve the status field from the user_events document
                                        String status = doc.getString("status");

                                        // Use the username from the parent user document and status from user_events
                                        Map<String, String> attendee = new HashMap<>();
                                        attendee.put("userName", userName != null ? userName : "Unknown User");
                                        attendee.put("status", status != null ? status : "Pending");

                                        attendeesList.add(attendee);
                                    }
                                    // Set the adapter with the attendees list
                                    AttendeesAdapter adapter = new AttendeesAdapter(attendeesList);
                                    attendeesRecyclerView.setAdapter(adapter);
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load user events", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show());
    }
}
