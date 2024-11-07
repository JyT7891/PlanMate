package com.example.planmate.user.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planmate.databinding.FragmentHomeBinding;
import com.example.planmate.user.ui.task.*;
import com.example.planmate.user.ui.task.TaskAdapter;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize RecyclerView for tasks
        recyclerView = binding.recyclerViewTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskAdapter = new TaskAdapter(new ArrayList<>());  // Initialize adapter with empty list
        recyclerView.setAdapter(taskAdapter);

        // Observe task list from ViewModel and update RecyclerView when data changes
        homeViewModel.getTaskList().observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.setTaskList(tasks);  // Update adapter data
            taskAdapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
