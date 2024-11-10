package com.example.planmate.user.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planmate.user.ui.event.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Event>> upcomingEvents; // LiveData for upcoming events
    private final FirebaseFirestore db; // Firebase Firestore instance

    public HomeViewModel() {
        upcomingEvents = new MutableLiveData<>(new ArrayList<>()); // Initialize with an empty list
        db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
        fetchUpcomingEvents(); // Fetch events from Firestore on initialization
    }

    public LiveData<List<Event>> getUpcomingEvents() {
        return upcomingEvents; // Return the LiveData for observers
    }

    // Method to fetch events from Firestore
    public void fetchUpcomingEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        events.add(event); // Add each event to the list
                    }
                    upcomingEvents.setValue(events); // Update LiveData with fetched events
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeViewModel", "Error fetching events", e);
                    upcomingEvents.setValue(new ArrayList<>()); // Set to empty list on failure
                });
    }
}
