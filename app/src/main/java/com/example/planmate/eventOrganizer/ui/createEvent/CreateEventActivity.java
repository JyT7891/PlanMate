package com.example.planmate.eventOrganizer.ui.createEvent;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planmate.R;
import com.example.planmate.eventOrganizer.ui.rsvp.SelectAttendeesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity {

    private EditText edtEventName, edtEventLocation, edtEventDescription;
    private TextView tvEventDate;
    private Button btnSaveEvent;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String eventDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize views
        edtEventName = findViewById(R.id.edtEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        edtEventLocation = findViewById(R.id.edtEventLocation);
        edtEventDescription = findViewById(R.id.edtEventDescription);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);

        // Set up Date Picker
        tvEventDate.setOnClickListener(v -> showDatePickerDialog());

        // Save button action
        btnSaveEvent.setOnClickListener(v -> saveEvent());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    eventDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    tvEventDate.setText(eventDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveEvent() {
        String eventName = edtEventName.getText().toString().trim();
        String eventLocation = edtEventLocation.getText().toString().trim();
        String eventDescription = edtEventDescription.getText().toString().trim();

        if (eventName.isEmpty() || eventDate.isEmpty() || eventLocation.isEmpty() || eventDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

        // Prepare data for Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("eventDate", eventDate);
        eventData.put("eventLocation", eventLocation);
        eventData.put("eventDescription", eventDescription);
        eventData.put("createdAt", FieldValue.serverTimestamp());
        eventData.put("organizerId", organizerId);

        // Add event to Firestore and retrieve the event ID
        db.collection("events")
                .add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId();
                    // Navigate to SelectAttendeesActivity and pass eventId
                    Intent intent = new Intent(CreateEventActivity.this, SelectAttendeesActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(CreateEventActivity.this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
