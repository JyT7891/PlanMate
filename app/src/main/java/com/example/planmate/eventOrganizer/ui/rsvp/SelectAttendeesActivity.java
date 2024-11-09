package com.example.planmate.eventOrganizer.ui.rsvp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectAttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private String eventId;
    private List<String> selectedUserIds;
    private List<Map<String, String>> usersList; // To store userId and username pairs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_attendees);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");
        selectedUserIds = new ArrayList<>();
        usersList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        btnConfirm = findViewById(R.id.btnConfirm);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchUsersFromFirestore();

        btnConfirm.setOnClickListener(v -> saveAttendees());
    }

    private void fetchUsersFromFirestore() {
        db.collection("users")
                .whereEqualTo("userType", "user") // Filter for userType = "user"
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot) {
                        String userId = document.getId();
                        String username = document.getString("username");

                        // Check if the user is already invited to the event
                        if (username != null) {
                            db.collection("users").document(userId)
                                    .collection("user_events").document(eventId)
                                    .get()
                                    .addOnSuccessListener(eventDoc -> {
                                        if (!eventDoc.exists()) {
                                            // User is not yet invited to the event
                                            Map<String, String> userMap = new HashMap<>();
                                            userMap.put("userId", userId);
                                            userMap.put("username", username);
                                            usersList.add(userMap);

                                            // Update the adapter each time a non-invited user is added
                                            UserAdapter adapter = new UserAdapter(usersList, selectedUserIds);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to check user invitations: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch users: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }


    private void saveAttendees() {
        for (String userId : selectedUserIds) {
            Map<String, Object> userEventData = new HashMap<>();
            userEventData.put("eventId", eventId);
            userEventData.put("role", "attendee");
            userEventData.put("status", "Pending"); // Set default RSVP status to "Pending"
            userEventData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

            db.collection("users").document(userId)
                    .collection("user_events").document(eventId)
                    .set(userEventData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Attendee added successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add attendee: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
        finish();
    }
}
