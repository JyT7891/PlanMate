package com.example.planmate.eventOrganizer.ui.rsvp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    private SearchView searchView;
    private FirebaseFirestore db;
    private String eventId, eventName;
    private List<String> selectedUserIds;
    private List<Map<String, String>> usersList; // To store userId and username pairs
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_attendees);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName"); // Ensure eventName is passed from the previous activity
        selectedUserIds = new ArrayList<>();
        usersList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        btnConfirm = findViewById(R.id.btnConfirm);
        searchView = findViewById(R.id.searchView); // Initialize the searchView

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(usersList, selectedUserIds);
        recyclerView.setAdapter(adapter);

        fetchUsersFromFirestore();

        btnConfirm.setOnClickListener(v -> saveAttendees());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return false;
            }
        });
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

                                            // Notify adapter of data change
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to check user invitations: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch users: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveAttendees() {
        // First, collect all user IDs in the system
        List<String> allUserIds = new ArrayList<>();
        db.collection("users")
                .whereEqualTo("userType", "user") // Only regular users
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot) {
                        allUserIds.add(document.getId());
                    }

                    // Send notifications to selected users and invite them to the event
                    for (String userId : selectedUserIds) {
                        Map<String, Object> userEventData = new HashMap<>();
                        userEventData.put("eventId", eventId);
                        userEventData.put("role", "attendee");
                        userEventData.put("status", "Pending"); // Set default RSVP status to "Pending"
                        userEventData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

                        db.collection("users").document(userId)
                                .collection("user_events").document(eventId)
                                .set(userEventData)
                                .addOnSuccessListener(aVoid -> {
                                    // Send invitation notification to the user
                                    sendNotificationToUser(userId, eventId, "You have been invited to an event: " + eventName);
                                    Toast.makeText(this, "Attendee added successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add attendee: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }

                    // Send event recommendation notification to users who were not selected
                    List<String> nonSelectedUserIds = new ArrayList<>(allUserIds);
                    nonSelectedUserIds.removeAll(selectedUserIds);
                    for (String userId : nonSelectedUserIds) {
                        sendNotificationToUser(userId, eventId, "Check out this new event: " + eventName);
                    }

                    // Finish the activity after saving attendees and sending notifications
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch users: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void sendNotificationToUser(String userId, String eventId, String message) {
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("eventId", eventId);
        notificationData.put("message", message);
        notificationData.put("isRead", false);
        notificationData.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        db.collection("notifications").add(notificationData)
                .addOnSuccessListener(docRef -> Log.d("Notification", "Notification sent to user: " + userId))
                .addOnFailureListener(e -> Log.e("Notification", "Failed to send notification: " + e.getMessage()));
    }


    private void filterUsers(String query) {
        List<Map<String, String>> filteredList = new ArrayList<>();
        for (Map<String, String> user : usersList) {
            if (user.get("username").toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        adapter.updateList(filteredList);
    }
}
