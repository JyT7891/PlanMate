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
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventAdapter; // Create an adapter for upcoming events
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize RecyclerView for events
        eventsRecyclerView = binding.recyclerViewEvents; // Assuming you have this in your layout
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventAdapter = new EventAdapter(new ArrayList<>()); // Adapter for displaying events
        eventsRecyclerView.setAdapter(eventAdapter);

        // Observe upcoming events from ViewModel and update RecyclerView when data changes
        homeViewModel.getUpcomingEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.setEventList(events); // Update adapter data
            eventAdapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
