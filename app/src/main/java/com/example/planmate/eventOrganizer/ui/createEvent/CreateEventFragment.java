package com.example.planmate.eventOrganizer.ui.createEvent;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.planmate.R;
import com.example.planmate.eventOrganizer.ui.rsvp.SelectAttendeesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateEventFragment extends Fragment {

    private EditText edtEventName, edtEventDate, edtEventLocation, edtEventDescription;
    private Button btnSaveEvent;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        edtEventName = view.findViewById(R.id.edtEventName);
        edtEventDate = view.findViewById(R.id.edtEventDate);
        edtEventLocation = view.findViewById(R.id.edtEventLocation);
        edtEventDescription = view.findViewById(R.id.edtEventDescription);
        btnSaveEvent = view.findViewById(R.id.btnSaveEvent);

        // Set up date picker for the event date field
        edtEventDate.setOnClickListener(v -> showDatePickerDialog());

        // Save button action
        btnSaveEvent.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    edtEventDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveEvent() {
        String eventName = edtEventName.getText().toString().trim();
        String eventDate = edtEventDate.getText().toString().trim();
        String eventLocation = edtEventLocation.getText().toString().trim();
        String eventDescription = edtEventDescription.getText().toString().trim();

        if (eventName.isEmpty() || eventDate.isEmpty() || eventLocation.isEmpty() || eventDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the organizer's ID from the authenticated user
        String organizerId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "unknown";

        // Prepare event data for Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventName", eventName);
        eventData.put("eventDate", eventDate);
        eventData.put("eventLocation", eventLocation);
        eventData.put("eventDescription", eventDescription);
        eventData.put("createdAt", FieldValue.serverTimestamp());
        eventData.put("organizerId", organizerId); // Add organizerId to the event data

        // Add event to Firestore
        db.collection("events")
                .add(eventData)
                .addOnSuccessListener(documentReference -> {
                    String eventId = documentReference.getId(); // Get the newly created event ID
                    Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();

                    // Clear fields after saving
                    edtEventName.setText("");
                    edtEventDate.setText("");
                    edtEventLocation.setText("");
                    edtEventDescription.setText("");

                    // Navigate to SelectAttendeesActivity with the eventId
                    Intent intent = new Intent(getActivity(), SelectAttendeesActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create event: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
