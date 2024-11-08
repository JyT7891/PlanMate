package com.example.planmate.user.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.planmate.user.ui.event.Event; // Import statement for Event
import com.example.planmate.user.ui.task.Task;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Task>> taskList;
    private final MutableLiveData<List<Event>> upcomingEvents; // LiveData for upcoming events

    public HomeViewModel() {
        taskList = new MutableLiveData<>(new ArrayList<>());  // Initialize with an empty list
        upcomingEvents = new MutableLiveData<>(new ArrayList<>()); // Initialize with an empty list
        loadSampleTasks();  // Load sample data
        loadSampleEvents(); // Load sample events
    }

    public LiveData<List<Task>> getTaskList() {
        return taskList;
    }

    public LiveData<List<Event>> getUpcomingEvents() {
        return upcomingEvents; // Return the LiveData for observers
    }

    // Load sample tasks (replace this with actual data fetching logic)
    private void loadSampleTasks() {
        List<Task> sampleTasks = new ArrayList<>();
        sampleTasks.add(new Task("Team Meeting", "2024-11-01", "10:00 AM"));
        sampleTasks.add(new Task("Project Kick-off", "2024-11-05", "3:00 PM"));
        sampleTasks.add(new Task("Workshop", "2024-11-10", "1:00 PM"));
        taskList.setValue(sampleTasks);
    }

    // Load sample events (replace this with actual data fetching logic)
    private void loadSampleEvents() {
        List<Event> sampleEvents = new ArrayList<>();
        sampleEvents.add(new Event("1", "Tech Conference", "2024-11-15", "10:00 AM", "Location A", "A conference about tech innovations.", new ArrayList<>()));
        sampleEvents.add(new Event("2", "Networking Event", "2024-11-20", "5:00 PM", "Location B", "An opportunity to connect with industry professionals.", new ArrayList<>()));
        upcomingEvents.setValue(sampleEvents); // Set the sample events
    }

    // Method to add a new task
    public void addTask(Task task) {
        List<Task> currentTasks = taskList.getValue();
        if (currentTasks != null) {
            currentTasks.add(task);
            taskList.setValue(currentTasks);  // Update LiveData with the new list
        }
    }

    // Method to fetch events from Firestore or other data sources
    public void fetchUpcomingEvents() {
        // Simulate fetching data (replace this logic with your actual data fetching)
        try {
            // Example logic to fetch events from Firestore (replace this with actual fetching logic)
            List<Event> fetchedEvents = new ArrayList<>();
            // Assume you fetch data and add it to fetchedEvents list
            upcomingEvents.setValue(fetchedEvents);
        } catch (Exception e) {
            Log.e("HomeViewModel", "Error fetching events", e);
            upcomingEvents.setValue(new ArrayList<>()); // Set to an empty list on error
        }
    }
}
