package com.example.planmate.user.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.planmate.R;
import com.example.planmate.user.ui.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventId;
    private String userId;
    private Button acceptButton, declineButton;
    private TextView eventTitle, eventDescription, eventDate, eventLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize Firestore and get the current user ID
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        eventId = getIntent().getStringExtra("eventId");

        // Initialize UI components
        eventTitle = findViewById(R.id.eventTitle);
        eventDescription = findViewById(R.id.eventDescription);
        eventDate = findViewById(R.id.eventDate);
        eventLocation = findViewById(R.id.eventLocation);
        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);

        // Load event details
        loadEventDetails();

        // Set click listeners for RSVP buttons
        acceptButton.setOnClickListener(v -> updateRSVPStatus("Accepted"));
        declineButton.setOnClickListener(v -> updateRSVPStatus("Declined"));
    }

    private void loadEventDetails() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Set event details in the UI
                        eventTitle.setText(documentSnapshot.getString("eventName"));
                        eventDescription.setText(documentSnapshot.getString("eventDescription"));
                        eventDate.setText(documentSnapshot.getString("eventDate"));
                        eventLocation.setText(documentSnapshot.getString("eventLocation"));
                    } else {
                        Toast.makeText(EventDetailsActivity.this, "Event details not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show());
    }

    private void updateRSVPStatus(String status) {
        db.collection("users").document(userId)
                .collection("user_events").document(eventId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EventDetailsActivity.this, "RSVP " + status, Toast.LENGTH_SHORT).show();

                    // Redirect to the Home page
                    Intent intent = new Intent(EventDetailsActivity.this, HomeActivity.class); // Replace with your HomeActivity class
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // End EventDetailsActivity to remove it from the back stack
                })
                .addOnFailureListener(e -> Toast.makeText(EventDetailsActivity.this, "Failed to update RSVP", Toast.LENGTH_SHORT).show());
    }

}
