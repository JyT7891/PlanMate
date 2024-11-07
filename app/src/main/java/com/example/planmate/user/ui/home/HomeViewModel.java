package com.example.planmate.user.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planmate.user.ui.task.Task; // Import statement for Task

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Task>> taskList;

    public HomeViewModel() {
        taskList = new MutableLiveData<>(new ArrayList<>());  // Initialize with an empty list
        loadSampleTasks();  // Load sample data
    }

    public LiveData<List<Task>> getTaskList() {
        return taskList;
    }

    // Load sample tasks (replace this with actual data fetching logic)
    private void loadSampleTasks() {
        List<Task> sampleTasks = new ArrayList<>();
        sampleTasks.add(new Task("Team Meeting", "2024-11-01", "10:00 AM"));
        sampleTasks.add(new Task("Project Kick-off", "2024-11-05", "3:00 PM"));
        sampleTasks.add(new Task("Workshop", "2024-11-10", "1:00 PM"));

        taskList.setValue(sampleTasks);
    }

    // Method to add a new task
    public void addTask(Task task) {
        List<Task> currentTasks = taskList.getValue();
        if (currentTasks != null) {
            currentTasks.add(task);
            taskList.setValue(currentTasks);  // Update LiveData with the new list
        }
    }
}
